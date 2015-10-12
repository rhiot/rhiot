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
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class SshClient {

    // Constants

    private static final int DEFAULT_SSH_PORT = 22;

    // Configuration members

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    // Collaborators

    private final JSch jsch = new JSch();

    // Constructors

    public SshClient(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public SshClient(String host, String username, String password) {
        this(host, DEFAULT_SSH_PORT, username, password);
    }

    public List<String> command(String command) {
        ListSshClientOutputCollector outputCollector = new ListSshClientOutputCollector();
        command(command, outputCollector);
        return outputCollector.lines();
    }

    public void printCommand(String command) {
        command(command, new StdoutSshClientOutputCollector());
    }

    public void command(String command, SshClientOutputCollector outputCollector) {
        Session session = null;
        Channel channel = null;
        try {
            session = connect();
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);


            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String msg = null;
            while ((msg = in.readLine()) != null) {
                outputCollector.collect(msg);
            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException | IOException jsche) {
            throw new RuntimeException(jsche);
        } finally {
            if (channel != null) {
                channel.disconnect();
                session.disconnect();
            }
        }
    }

    public void scp(InputStream inputStream, File destination, Boolean toRoot) {
        Session session = null;
        Channel channel = null;
        try {
            session = connect();
            channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp channelSftp = (ChannelSftp) channel;
            if (!Boolean.TRUE.equals(toRoot)) {
                mkdirs(channelSftp, destination.getParent());
                channelSftp.cd(destination.getParent());
            }
            channelSftp.put(inputStream, destination.getName());

            channelSftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException jsche) {
            throw new RuntimeException(jsche);
        } finally {
            if (channel != null) {
                channel.disconnect();
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