package net.froihofer.util.jboss;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * This file allows to add users to the JBoss authentication database. Data
 * are stored in the application-users.properties and
 * application-roles.properties files.
 *
 * @author Lorenz Froihofer
 * @version $Id: WildflyAuthDBHelper.java 215:35f622b551ee 2020/12/17 21:10:17 Lorenz Froihofer $
 */
public class WildflyAuthDBHelper {
    private static final Logger log = LoggerFactory.getLogger(WildflyAuthDBHelper.class);
    private static final String NEWLINE = System.getProperty("line.separator");

    private File jbossHomePath;
    private File applicationUsersDbStandalone;
    private File rolesDbStandalone;
    private File applicationUsersDbDomain;
    private File rolesDbDomain;


    /**
     * Constructs a new instance based on the JBOSS_HOME environment variable.
     *
     * @throws IllegalStateException    if the JBOSS_HOME environment variable is not set.
     * @throws IllegalArgumentException if the location does not seem to be a JBoss location.
     */
    public WildflyAuthDBHelper() {
        String jbossHome = System.getenv("JBOSS_HOME");
        if (jbossHome == null) throw new IllegalStateException("JBOSS_HOME is not set");
      init(new File(jbossHome));
    }

    /**
     * Constructs a new instance with the path pointing
     * to a JBOSS_HOME location.
     *
     * @param jbossHomePath path to the JBOSS_HOME location.
     * @throws IllegalArgumentException if the location does not seem to be a JBoss location.
     */
    public WildflyAuthDBHelper(File jbossHomePath) {
      init(jbossHomePath);
    }

    public static void removeUser(String user) {
        throw new UnsupportedOperationException("removeUser(user) is left as an exercise");
    }

    private static String getInitialCommentFromPropertiesFile(File propsFile) throws IOException {
        String lastLine;
        List<String> lines;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(propsFile)))) {
            String line = StringUtils.trim(br.readLine());
            boolean stillComment = line.startsWith("#");
            line = line.replaceFirst("#", "");
            final String result = "";
            lastLine = line;
            lines = new ArrayList<>();
            while (stillComment && line != null) {
                lines.add(line);
                lastLine = line;
                line = StringUtils.trim(br.readLine());
                stillComment = line != null && line.startsWith("#");
            }
        }
        if (lastLine.matches("#(Sun|Mon|Tue|Wed|Thu|Fri|Sat) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).*")) {
            lines.remove(lines.size() - 1);
        }
        return StringUtils.join(lines.iterator(), NEWLINE);
    }

    private static void addUserToDBs(String user, String password, String[] roles, File usersDb, File rolesDb) throws IOException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
        //Process usersDB
        String initialComment = getInitialCommentFromPropertiesFile(usersDb);
        Properties users = new Properties();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(usersDb));
        users.load(isr);
        isr.close();
        String passwordEntry = DatatypeConverter.printHexBinary(md.digest((user + ":ApplicationRealm:" + password).getBytes())).toLowerCase();
        users.put(user, passwordEntry);
        PrintWriter pw = new PrintWriter(usersDb);
        users.store(pw, initialComment);
        pw.close();

        // Process rolesDB
        Properties rolesProps = new Properties();
        isr = new InputStreamReader(new FileInputStream(rolesDb));
        rolesProps.load(isr);
        isr.close();
        rolesProps.put(user, StringUtils.join(roles, ","));
        pw = new PrintWriter(rolesDb);
        rolesProps.store(pw, initialComment);
        pw.close();
    }

    private void init(File jbossHomePath) {
        if (!jbossHomePath.exists() ||
                !new File(jbossHomePath.getAbsolutePath() + "/standalone").exists() ||
                !new File(jbossHomePath.getAbsolutePath() + "/domain").exists()) {
            throw new IllegalArgumentException("\"" + jbossHomePath.getAbsolutePath() + "\""
                    + " does not seem to be a Wildfly location.");
        }
        this.jbossHomePath = jbossHomePath;
      applicationUsersDbStandalone = new File(jbossHomePath.getAbsolutePath() + "/standalone/configuration/application-users.properties");
      rolesDbStandalone = new File(jbossHomePath.getAbsolutePath() + "/standalone/configuration/application-roles.properties");
      applicationUsersDbDomain = new File(jbossHomePath.getAbsolutePath() + "/domain/configuration/application-users.properties");
      rolesDbDomain = new File(jbossHomePath.getAbsolutePath() + "/domain/configuration/application-roles.properties");
    }

    /**
     * Adds a user to the JBoss authentication database.
     *
     * @param user     username of the user
     * @param password password of the user
     * @param roles    roles to be assigned to the user
     * @throws IOException if errors pop up during file access
     */
    public void addUser(String user, String password, String[] roles) throws IOException {
        addUserToDBs(user, password, roles, applicationUsersDbStandalone, rolesDbStandalone);
    }

    public void deleteUser(String user, String password, String[] roles) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String userToDelete = user + "=" + DatatypeConverter.printHexBinary(md.digest((user + ":ApplicationRealm:" + password).getBytes())).toLowerCase();
          deleteFromDB(applicationUsersDbStandalone, userToDelete);
            String rolesToDelete = user + "=" + Arrays.toString(roles);
          deleteFromDB(rolesDbStandalone, rolesToDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFromDB(File originalFile, String lineToDelete) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(originalFile))) {
            File tempFile = new File(originalFile.getAbsolutePath() + ".tmp");
            try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.trim().equalsIgnoreCase(lineToDelete)) {
                        writer.println(line);
                        writer.flush();
                    }
                }
            }
            Files.delete(applicationUsersDbStandalone.toPath());
            if (!tempFile.renameTo(applicationUsersDbStandalone))
                throw new IllegalStateException("Could not rename file: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
