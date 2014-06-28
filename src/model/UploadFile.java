package model;

import com.bmarius.sockets.WebSocketClient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.commons.codec.binary.Base64;

/**
 * @author Szymon Skrzyński <skrzyński.szymon@gmail.com>
 */
public class UploadFile {

    public String fileName;
    public String fileExtension;
    public String mimeType;
    public String hash;
    public WebSocketClient client;
    public entities.File fileObj;
    public String pathToFile;
    public Long totalSize;
    public Long uploadSize = new Long(0);

    public UploadFile(entities.File parentFolder, String fileName, String mimeType, Long totalSize) throws UploadFileException {
        this._checkMimeType(mimeType);
        this.fileName = this._getFileName(fileName);
        this.fileExtension = this._getFileExension(fileName);
        this.mimeType = mimeType;
        this.hash = this._generateHash();
        this.totalSize = totalSize;
        this.pathToFile = mywebsocket.Config.uploadFilePath + "/" + this.hash;

        //  zapisuje do bazy
        this.fileObj = new entities.File();
        this.fileObj.setName(this.fileName);
        this.fileObj.setParentId(parentFolder);
        this.fileObj.setCreatedAt(new Date());
        this.fileObj.setUpload((short) 0);
        this.fileObj.setMimeType(this.mimeType);
        this.fileObj.setHashName(this.hash);

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(this.fileObj);
        session.getTransaction().commit();

        //  tworzy pusty plik
        try {
            File file = new File(this.pathToFile);
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Przypisuje ten obiekt do sesji z przeglądarką
     * 
     * @param WebSocketClient client
     */
    public void saveToClient(WebSocketClient client) {
        client.uploadingFiles.put(this.hash, this);
        this.client = client;
    }

    private String _getFileName(String fileName) {
        String pattern = "(.*)\\.([a-zA-Z]{1,})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(fileName);
        m.find();
        return m.group(1);
    }

    private String _getFileExension(String fileName) {
        String pattern = "(.*)\\.([a-zA-Z]{1,})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(fileName);
        m.find();
        return m.group(2);
    }

    /**
     * Sprawdza, czy typ pliku jest akceptowalny. Jeżeli nie rzuca wyjątek.
     *
     * @param String mimeType
     */
    private void _checkMimeType(String mimeType) throws UploadFileException {
        switch (mimeType) {
            case "image/jpeg":
            case "image/jpg":
            case "image/png":
            case "image/gif":
            case "image/bmp":
                
            case "text/xml":
            case "text/plain":
                
            case "video/mp4":
                return;
            default:
                throw new UploadFileException();
        }
    }

    //  TODO: na razie hashe mogą się powtórzyć - trzeba poprawić
    /**
     * Generuje unikalny hash dla identyfikacji pliku.
     *
     * @return String
     */
    private String _generateHash() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String string = dateFormat.format(date) + System.nanoTime();
        return this._sha1(string);
    }

    private String _sha1(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = this._byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    private String _byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public void append(String base64) {
        FileOutputStream output = null;
        base64 = base64.substring("data:;base64,".length());
        System.out.println(base64);
        
        try {
            byte[] bytes = Base64.decodeBase64(base64);
            output = new FileOutputStream(this.pathToFile, true);
            output.write(bytes);
            this.uploadSize += bytes.length;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
