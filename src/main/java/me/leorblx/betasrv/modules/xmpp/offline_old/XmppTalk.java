package me.leorblx.betasrv.modules.xmpp.offline_old;

import me.leorblx.betasrv.utils.Concurrency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Future;

public class XmppTalk
{
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int personaId;

    public XmppTalk(Socket socket)
    {
        this.socket = socket;
        setReaderWriter();
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
        setReaderWriter();
    }

    public Socket getSocket()
    {
        return socket;
    }

    private void setReaderWriter()
    {
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Future<String> read()
    {
        return Concurrency.submit(() -> {
            String msg = null;
            char[] buffer = new char[8192];
            int charsRead = 0;
            try {
                if (socket.isClosed())
                    return null;
                
                if ((charsRead = reader.read(buffer)) != -1) {
                    msg = new String(buffer).substring(0, charsRead);
                    System.out.println("C->S [" + msg + "]");
                }
            } catch (Exception e) {
                if (!e.getMessage().contains("Connection reset"))
                    e.printStackTrace();
            }
            return msg;
        });
    }

    public void write(String msg)
    {
        try {
            if (socket.isClosed())
                return;
            
            System.out.println("S->C [" + msg + "]");
            writer.write(msg);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPersonaId()
    {
        return personaId;
    }

    public void setPersonaId(int personaId)
    {
        this.personaId = personaId;
    }
}