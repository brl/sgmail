package com.subgraph.sgmail.identity.client;

import com.google.common.primitives.UnsignedLongs;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ReceiveRegistrationEmailTask implements Callable<MimeMessage> {

    private final static long CHECK_BACK_MESSAGES_PERIOD = TimeUnit.MINUTES.toMillis(5);

    private final String imapLogin;
    private final String imapPassword;
    private final String imapServer;
    private long requestId;


    private MimeMessage messageReceived;

    public ReceiveRegistrationEmailTask(String imapServer, String imapLogin, String imapPassword) {
        this.imapServer = imapServer;
        this.imapLogin = imapLogin;
        this.imapPassword = imapPassword;
        this.requestId = 0;
    }

    public synchronized void setRequestId(long requestId) {
       this.requestId = requestId;
       notifyAll();
    }

    @Override
    public MimeMessage call() throws Exception {
        final IMAPStore store = openStore();
        try {
            final IMAPFolder inbox = openInbox(store);
            final MimeMessage msg = searchFolder(inbox);
            if(msg != null) {
                inbox.close(false);
                return msg;
            }
            while(true) {
                inbox.idle();
                if(messageReceived != null) {
                    inbox.close(false);
                    return messageReceived;
                }
            }
        } finally {
            store.close();
        }
    }

    private IMAPStore openStore() throws MessagingException {
        final Session session = Session.getInstance(new Properties());
        final IMAPStore store = (IMAPStore) session.getStore("imaps");
        store.connect(imapServer, imapLogin, imapPassword);
        return store;
    }

    private IMAPFolder openInbox(IMAPStore store) throws MessagingException {
        final IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
        inbox.open(IMAPFolder.READ_WRITE);
        inbox.addMessageCountListener(createMessageCountListener());
        return inbox;
    }

    private MimeMessage searchFolder(IMAPFolder folder) throws MessagingException {
        final int count = folder.getMessageCount();
        final long now = new Date().getTime();
        int idx = count;
        while(idx > 0) {
            Message m = folder.getMessage(idx);
            if(now - m.getReceivedDate().getTime() > CHECK_BACK_MESSAGES_PERIOD) {
                return null;
            }
            if(messageMatchesRequestId(m)) {
                return (MimeMessage) m;
            }
            idx -= 1;
        }
        return null;
    }

    private MessageCountListener createMessageCountListener() {
        return new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                for(Message m: messageCountEvent.getMessages()) {
                    processIncomingMessage(m);
                }
            }
        };
    }

    private void processIncomingMessage(Message message) {
        if(messageMatchesRequestId(message)) {
            messageReceived = (MimeMessage) message;
        }
    }

    private boolean messageMatchesRequestId(Message message)  {
        try {
            final String[] headers = message.getHeader("X-SGMAIL-IDENTITY-REGISTRATION");
            if(headers == null || headers.length != 1) {
                return false;
            }
            return headerMatchesRequestId(headers[0]);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

    }
    private boolean headerMatchesRequestId(String header) {
        final String[] parts = header.split(":");
        if(parts.length != 2) {
            return false;
        }
        synchronized (this) {
            while(requestId == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return UnsignedLongs.parseUnsignedLong(parts[0], 16) == requestId;
    }
}