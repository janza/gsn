package gsn.wrappers.rasip;

import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.utils.Base64;
import gsn.utils.ParamParser;
import gsn.wrappers.AbstractWrapper;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class IPCamWrapper extends AbstractWrapper {
	
	private final transient Logger logger = Logger.getLogger(IPCamWrapper.class);
	private static int threadCounter = 0;
    
    private transient DataField[] outputStructure = new DataField[]{new DataField("picture", "binary", "USB camera picture.")};
    
	private static final String WRAPPER_NAME = "IPCamWrapper";

    private static final int DEFAULT_SAMPLING_RATE = 1;
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;
    private static final int DEFAULT_DEVICE_ID = 0;
    private static final int DEFAULT_RATE = 1000;
    
    private int samplingRate = DEFAULT_SAMPLING_RATE;
    private int rate = DEFAULT_RATE;
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private int deviceId = DEFAULT_DEVICE_ID;
    private static String schema = "http://";
    private static String serverName="161.53.67.96";
    private static String port = "8080";
    private static String user = "ivica";
    private static String pass = "hiperion";
    
    @Override
    public boolean sendToWrapper(String action, String[] paramNames,
                              Object[] paramValues){
        try {
            if(action.equals("MOVE") && paramValues.length > 1)
            {
                int X = Integer.parseInt(paramValues[0].toString());
                int Y = 0;
                if(paramValues.length == 2)
                    Y = Integer.parseInt(paramValues[1].toString());

                URL url = new URL(schema + serverName + port + "/cgi/ptdc.cgi?command=set_relative_pos&posX=-"+ X + "&posY=" + Y) ;
                String authStr = user + ":" + pass;
                String authEncoded = Base64.encodeToString(authStr.getBytes(), false);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getContent();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;
    }
   
    @Override
    public void run() {
        while (isActive()) {

            ByteArrayOutputStream baos = null;
            try {
                URL url = new URL("http://161.53.67.95:8080/video/mjpg.cgi?profileid=1");

                String authStr = user + ":" + pass;
                String authEncoded = Base64.encodeToString(authStr.getBytes(), false);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Basic " + authEncoded);

                baos = new ByteArrayOutputStream();

                InputStream webStream = (InputStream) connection.getContent();

                try(IPKameraStreamWrapper ipWrapper = new IPKameraStreamWrapper(webStream))
                {
                    ipWrapper.writeNextImage(baos);

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                byte[] imageInByte = baos.toByteArray();
                baos.close();

                StreamElement streamElement = new StreamElement(new String[]{ "picture"},
                        new Byte[]{DataTypes.BINARY},
                        new Serializable[]{imageInByte},
                        System.currentTimeMillis());
                postStreamElement(streamElement);
                Thread.sleep(rate);

            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
    }
    
    @Override
    public DataField[] getOutputFormat() {
         return outputStructure;
    }

    @Override
    public boolean initialize() {
        try {
        	
            setName(WRAPPER_NAME + (++threadCounter));
            
            AddressBean addressBean = getActiveAddressBean();
            if (addressBean.getPredicateValue("sampling-rate") != null) {
                samplingRate = ParamParser.getInteger(addressBean.getPredicateValue("sampling-rate"), DEFAULT_SAMPLING_RATE);
                if (samplingRate <= 0) {
                    samplingRate = DEFAULT_SAMPLING_RATE;
                }
            }
            
            if(addressBean.getPredicateValue("image-width") != null){
                width = ParamParser.getInteger(addressBean.getPredicateValue("image-width"), DEFAULT_WIDTH);
                if (width <= 0) {
                    width = DEFAULT_WIDTH;
                }
            }
            
            if(addressBean.getPredicateValue("image-height") != null){
                height = ParamParser.getInteger(addressBean.getPredicateValue("image-height"), DEFAULT_HEIGHT);
                if (height <= 0) {
                    height = DEFAULT_HEIGHT;
                }
            }
            
            if(addressBean.getPredicateValue("device-id") != null){
                deviceId = ParamParser.getInteger(addressBean.getPredicateValue("device-id"), DEFAULT_DEVICE_ID);
                if (deviceId <= 0) {
                    deviceId = DEFAULT_DEVICE_ID;
                }
            }
            
            return true;
        } catch (Exception ex) {
            logger.error(ex);
        }
        return false;
    }

    @Override
    public void dispose() {
        threadCounter--;
    }

    @Override
    public String getWrapperName() {
        return WRAPPER_NAME;
    }
    
    private BufferedImage resizeImage(BufferedImage image) throws IOException{
        return Thumbnails.of(image).size(this.width, this.height).asBufferedImage();
    }

}
