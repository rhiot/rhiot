/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.utils.ssh.client;

import com.jcraft.jsch.*;

import java.io.*;

import static org.apache.commons.io.IOUtils.toByteArray;

class SshClient {

    // Constants

    private static final int DEFAULT_SSH_PORT = 22

    // Configuration members

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    // Collaborators

    private final def jsch = new JSch()

    // Constructors

    SshClient(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    SshClient(String host, String username, String password) {
        this(host, DEFAULT_SSH_PORT, username, password);
    }

    private <T> T executeSession(String channelType, ChannelCallback<T> callback) {
        Channel channel = null;
        Session session = null;
        try {
            session = connect();
            channel = session.openChannel(channelType);
            callback.onChannel(channel)
        } catch (JSchException | IOException jsche) {
            throw new RuntimeException(jsche);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if(session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public void checkConnection() {
        executeSession("exec", new ChannelCallback() {
            @Override
            Object onChannel(Channel channel) {
                return null
            }
        })
    }

    public List<String> command(String commandToExecute) {
        ListSshClientOutputCollector outputCollector = new ListSshClientOutputCollector();
        command(commandToExecute, outputCollector);
        return outputCollector.lines();
    }

    public void printCommand(String commandToExecute) {
        command(commandToExecute, new StdoutSshClientOutputCollector());
    }

    public void command(String command, SshClientOutputCollector outputCollector) {
        executeSession("exec", new ChannelCallback() {
            @Override
            Object onChannel(Channel channel) {
                ((ChannelExec) channel).setCommand(command);

                BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();

                String msg = null;
                while ((msg = br.readLine()) != null) {
                    outputCollector.collect(msg);
                }
                return null
            }
        })
    }

    void scp(InputStream inputStream, File destination) {
        Session session = null;
        try {
            session = connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp channelSftp = (ChannelSftp) channel;
            mkdirs(channelSftp, destination.getParent());
            channelSftp.cd(destination.getParent());
            channelSftp.put(inputStream, destination.getName());

            channelSftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException jsche) {
            throw new RuntimeException(jsche);
        } finally {
            if (session!= null) {
                session.disconnect();
            }
        }
    }

    public byte[] scp(File from) {
        Session session = null;
        try {
            session = connect()
            def channel = (ChannelSftp) session.openChannel("sftp")
            channel.connect()

            if(from.getParent() != null) {
                channel.cd(from.getParent())
            }
            toByteArray(channel.get(from.getName()));
        } catch (SftpException e) {
            if(e.id == 2) {
                return null;
            }
            throw new RuntimeException(e);
        } catch (IOException | JSchException jsche) {
            throw new RuntimeException(jsche);
        } finally {
            if(session != null) {
                session.disconnect();
            }
        }
    }

    private Session connect() {
        try {
            Session session = jsch.getSession(username, host, port);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);
            session.connect();
            return session;
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    private void mkdirs(ChannelSftp ch, String path) {
        if(path.equals("/")) {
            return;
        }
        try {
            String[] folders = path.split("/");
            if (folders[0].isEmpty()) folders[0] = "/";
            String fullPath = folders[0];
            for (int i = 1; i < folders.length; i++) {
                Vector ls = ch.ls(fullPath);
                boolean isExist = false;
                for (Object o : ls) {
                    if (o instanceof ChannelSftp.LsEntry) {
                        ChannelSftp.LsEntry e = (ChannelSftp.LsEntry) o;
                        if (e.getAttrs().isDir() && e.getFilename().equals(folders[i])) {
                            isExist = true;
                        }
                    }
                }
                if (!isExist && !folders[i].isEmpty()) {
                    ch.mkdir(fullPath + folders[i]);
                }
                fullPath = fullPath + folders[i] + "/";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}