package com.augmentum.upload.tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


/**
 * Avatar upload tool class
 * 
 * This class mainly to implement upload avatar features include upload a
 * original picture onto the server, save the tailored avatar, check the type of
 * the upload picture, delete the original upload picture.
 * 
 * @author John.Li
 * @see GifDecoder.class
 * @see AnimatedGifEncoder.class
 * @see LZWEncoder.class
 * @see NeQuant.class
 * @see MathUtil.class
 * @see MD5.class
 * @since 1.0 09.24.2013
 */
@Controller
public class uploadController extends HttpServlet {

    private static final long serialVersionUID = 2335601104050373991L;

    // use log4j to racord the necessary infomation
    private Logger logger = Logger.getLogger(this.getClass());
    private static final String UPLOAD_DIRECTORY = "upload/";
    private static final String[] IMAGE_TYPES = { "jpg", "jpeg", "png", "gif",
            "bmp" };
    private static final int IS_RIGHT_TYPE = 1;
    private static final int NOT_RIGHT_TYPE = 0;
    private static final double INIT_X_COODINATE = 0.0;
    private static final double INIT_Y_COODINATE = 0.0;
    private static final double INIT_WIDTH = 150.0;
    private static final double INIT_HEIGHT = 150.0;
    private static final double INIT_SRC_CHANGE_WIDTH = 150.0;
    private static final int ZOOM_WIDTH = 150;
    private static final int ZOOM_HEIGHT = 150;

    /**
     * upload the image onto the server, and then return the URL to the browser.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ModelAndView
     */
    @RequestMapping(value = "/uploadImg.do", method = RequestMethod.POST)
    public ModelAndView uploadImg(HttpServletRequest request,
            HttpServletResponse response) {

        // TODO get user from session to record information.
        // User user = (User) getSession().get("user");

        // set the name of the logiacl view, the ViewResolver will get the
        // specify view name by the this name.
        ModelAndView mv = new ModelAndView("index");
        String sourcePath = request.getSession().getServletContext()
                .getRealPath("/")
                + UPLOAD_DIRECTORY;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("imgFile");
        logger.info(/* user + */" is uploading a picture "
                + multipartFile.getOriginalFilename());

        // deal with the url, make the browser can recognize it.
        sourcePath = sourcePath.replace("\\", "/");

        if (!isRightFileType(multipartFile)) {
            logger
                    .warn(/* user + */" has uploaded a file with not allowed type.");
            mv.addObject("errorFileType", "errorFileType");
            return mv;
        }
        File fileDirectory = new File(sourcePath);

        if (!fileDirectory.exists() && !fileDirectory.isDirectory()) {
            // if the file directory is not exist, create it.
            logger
                    .info("The directory where to restore the upload picture is not exist.");
            fileDirectory.mkdir();
        }
        File file = new File(sourcePath + multipartFile.getOriginalFilename());

        if (!file.exists()) {
            // if the file is not exist, create it.
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.warn("create file "
                        + multipartFile.getOriginalFilename() + "error.");
                logger.info(e);
            }
        }
        try {
            multipartFile.transferTo(file);
        } catch (IllegalStateException e) {
            logger.warn(e);
        } catch (IOException e) {
            logger.warn(e);
        }
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {
            logger.warn("read image " + file + " error!");
            logger.warn(e);
        }
        int w = bi.getWidth();
        int h = bi.getHeight();
        String imgSrc = "upload/" + multipartFile.getOriginalFilename();
        mv.addObject("imgSrc", imgSrc);
        mv.addObject("imgWidth", w);
        mv.addObject("imgHeight", h);
        mv.addObject("imgName", multipartFile.getOriginalFilename());

        return mv;
    }

    /**
     * save the tailored image of the origianl uploaded picture.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param imgName
     *            the name of the original uploaded picture.
     * @return ModelAndView
     * @throws Exception
     */
    @RequestMapping(value = "/saveCutImg.do", method = RequestMethod.POST)
    public ModelAndView saveCutImg(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("imgName") String imgName) {

        ModelAndView mv = new ModelAndView("index");

        if ("" == imgName || null == imgName) {
            return mv;
        }

        // TODO get user from session to record information.
        // User user = (User) getSession().get("user");
        logger.info(/* user + */"is tailoring the uploaded picture " + imgName);

        // TODO delete the origianl exist avatar.
        // String userAvatar = user.getAvatar();
        // if ("".equals(userAvatar) || null != userAvatar){
        // user.setAvatar("");
        // File file = new File(userAvatar);
        // file.deleteOnExit();
        // }

        // initiate the coodinate of the tailor area in the changed
        // uploaded picture.
        TailorCoodinate tailorCoodinate = new TailorCoodinate(INIT_X_COODINATE,
                INIT_Y_COODINATE, INIT_WIDTH, INIT_HEIGHT,
                INIT_SRC_CHANGE_WIDTH);
        processParameter(request, tailorCoodinate);
        ServletContext context = request.getSession().getServletContext();
        String path = context.getRealPath("/");
        String serverPath = path + UPLOAD_DIRECTORY;
        String sourcePath = path + UPLOAD_DIRECTORY + imgName;
        String imageType = imgName.substring(imgName.lastIndexOf(".") + 1);
        //TODO john.li
        // String avatarName = user.getName();
        String avatarPath = path + UPLOAD_DIRECTORY + "李强" + "." + imageType;

        // // encrypt the avatar image name on the server.
        // String encryptImgName = MD5.md5Encrypt(imgName);
        // String encryptSrcPath = serverPath + encryptImgName + "." +
        // imageType;

        // read in the original uploaded picture on the server.
        BufferedImage in = null;
        try {
            in = ImageIO.read(new File(sourcePath));
        } catch (IOException e) {
            logger.warn("read image " + sourcePath + " error!");
            logger.warn(e);
        }

        // get the emplify version of the tailor area.
        double rate = in.getWidth() / tailorCoodinate.srcChangedWidth;
        tailorCoodinate.x = tailorCoodinate.x * rate;
        tailorCoodinate.y = tailorCoodinate.y * rate;
        tailorCoodinate.width = tailorCoodinate.width * rate;
        tailorCoodinate.height = tailorCoodinate.width;

        // tailor and delete the original uploaded picture with the emplify
        // version tailor area.
        cutImage(serverPath, imgName, avatarPath, in, tailorCoodinate);
        deleteOriginalUploadImage(request, response, imgName);
        
        // zoom avatar.
        String targetPath = zoom(avatarPath, avatarPath, imageType, ZOOM_WIDTH, ZOOM_HEIGHT);
        // user.setAvatar(targetPath);
        response.setHeader("Pragma ", "No-cache ");
        response.setHeader("Cache-Control ", "no-cache ");
        response.setDateHeader("Expires ", 0);
        request.getSession().setAttribute("avatarPicture",
                UPLOAD_DIRECTORY + "李强" + "." + imageType);

        return mv;
    }

    /**
     * check and process the parameter from the front-end.
     * 
     * @param request
     *            HttpServletRequest
     * @param srcChangedWidth
     *            the width of the original picture after it has changed at
     *            front-end.
     * @param x
     *            the left-top x coodinate of the tailor area.
     * @param y
     *            the left-top y coodinate of the tailor area.
     * @param width
     *            width of the tailor area.
     * @param height
     *            height of the tailor area,
     */
    private void processParameter(HttpServletRequest request,
            TailorCoodinate tailorCoodinate) {

        String xStr = request.getParameter("x");
        String yStr = request.getParameter("y");
        String wStr = request.getParameter("width");
        String hStr = request.getParameter("height");
        String srcWidth = request.getParameter("srcChangedWidth");

        if (null != xStr && !"".equals(xStr)) {
            tailorCoodinate.x = (double) Integer.parseInt(xStr);
        }

        if (null != yStr && !"".equals(yStr)) {
            tailorCoodinate.y = (double) Integer.parseInt(yStr);
        }
        if (null != wStr && !"".equals(wStr)) {
            tailorCoodinate.width = (double) Integer.parseInt(wStr);
        }
        else {
            tailorCoodinate.width = INIT_WIDTH;
        }
        if (null != hStr && !"".equals(hStr)) {
            tailorCoodinate.height = (double) Integer.parseInt(hStr);
        }
        else {
            tailorCoodinate.height = INIT_HEIGHT;
        }

        if (null != srcWidth && !"".equals(srcWidth)) {
            tailorCoodinate.srcChangedWidth = (double) Integer
                    .parseInt(srcWidth);
        }
        else {
            tailorCoodinate.srcChangedWidth = INIT_SRC_CHANGE_WIDTH;
        }
    }

    /**
     * tailor the uploaded picture by their type.
     * 
     * @param imageType
     *            the type of the image.
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     * @param width
     *            width of the tailor area.
     * @param height
     *            height of the tailor area.
     * @param sourcePath
     *            the source of the image.
     * @param encryptSrcPath
     *            the encrypt source path of the image.
     * @param in
     *            read in the image from the server.
     * @throws IOException
     */
    private void cutImage(String serverPath, String imgName, String avatarPath,
            BufferedImage in, TailorCoodinate tailorCoodinate) {

        BufferedImage out = null;
        String imageType = imgName.substring(imgName.lastIndexOf(".") + 1);

        // if the type of the image is 'gif'.
        if (IMAGE_TYPES[3].equals(imageType)) {
            GifDecoder decoder = new GifDecoder();
            int status = decoder.read(serverPath + imgName);

            if (status != GifDecoder.STATUS_OK) {
                logger.warn("read image " + serverPath + imgName + " error!");
            }
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.start(avatarPath);
            encoder.setRepeat(decoder.getLoopCount());

            for (int i = 0; i < decoder.getFrameCount(); i++) {
                encoder.setDelay(decoder.getDelay(i));
                BufferedImage childImage = decoder.getFrame(i);
                BufferedImage image = childImage.getSubimage(
                        (int) tailorCoodinate.x, (int) tailorCoodinate.y,
                        (int) tailorCoodinate.width,
                        (int) tailorCoodinate.height);
                encoder.addFrame(image);
            }
            encoder.finish();
        }
        else { // if the type is not 'gif'.
            out = in.getSubimage((int) tailorCoodinate.x,
                    (int) tailorCoodinate.y, (int) tailorCoodinate.width,
                    (int) tailorCoodinate.height);
            try {
                ImageIO.write(out, imageType, new File(avatarPath));
            } catch (IOException e) {
                logger.warn("write image " + avatarPath + " error!");
                logger.warn(e);
            }
        }

    }

    /**
     * Check the type of the file.
     * 
     * @param multipartFile
     *            MultipartFile
     * @return boolean the type is allowed or not.
     */
    public boolean isRightFileType(MultipartFile multipartFile) {

        String imageName = multipartFile.getOriginalFilename();
        String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);
        String lowCaseType = imageType.toLowerCase();

        if ("" == imageType || null == imageType) {
            return false;
        }
        int typeLength = IMAGE_TYPES.length;
        int isRightType = NOT_RIGHT_TYPE;

        for (int i = 0; i < typeLength; i++) {
            if (IMAGE_TYPES[i].equals(lowCaseType)) {
                isRightType = IS_RIGHT_TYPE;
                break;
            }
        }

        if (NOT_RIGHT_TYPE == isRightType) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Delete the original uploaded picture.
     * 
     * @param request
     *            HttpServletRequest.
     * @param response
     *            HttpServletResponse.
     * @param imgName
     *            The name of the deleted image.
     */
    @RequestMapping(value = "/deleteImg.do")
    public void deleteOriginalUploadImage(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("imgName") String imgName) {

        String serverPath = request.getSession().getServletContext()
                .getRealPath("/")
                + UPLOAD_DIRECTORY;
        String filePath = serverPath + imgName;

        // deal with the url, make the browser can recognize it.
        filePath = filePath.replace("\\", "/");

        if ("".equals(imgName) || null == imgName) {
            return;
        }

        // delete the original uploaded picture.
        File file = new File(filePath);
        file.delete();
    }

    /**
     * zoom avatar
     * 
     * @param sourcePath
     *            the path of the picture which to be reduced.
     * @param targetPath
     *            the target path after it has been reduce.
     * @param width
     *            width of the zoom.
     * @param height
     *            height of the zoom.
     * 
     * @returns the target path after it has been reduce.
     * @throws IOException
     */
    public String zoom(String sourcePath, String targetPath, String formatName,
            int width, int height){
        
        File file = new File(sourcePath);
        
        if (!file.exists()) {
            logger.warn("not found the image ：" + sourcePath);
        }
        if (null == targetPath || targetPath.isEmpty())
            targetPath = sourcePath;

        formatName = formatName.toLowerCase();

        // .gif
        if (IMAGE_TYPES[3].equals(formatName)) {
            GifDecoder decoder = new GifDecoder();
            int status = decoder.read(sourcePath);
            if (status != GifDecoder.STATUS_OK) {
                logger.warn("read image " + sourcePath + " error!");
            }

            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.start(targetPath);
            encoder.setRepeat(decoder.getLoopCount());
            for (int i = 0; i < decoder.getFrameCount(); i++) {
                encoder.setDelay(decoder.getDelay(i));
                BufferedImage image = zoom(decoder.getFrame(i), width, height);
                encoder.addFrame(image);
            }
            encoder.finish();
        }
        else {
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                logger.warn("read image " + file + " error!");
                logger.warn(e);
            }
            BufferedImage zoomImage = zoom(image, width, height);
            try {
                ImageIO.write(zoomImage, formatName, new File(targetPath));
            } catch (IOException e) {
                logger.warn("write image " + targetPath + " error!");
                logger.warn(e);
            }
        }

        return targetPath;
    }

    /**
     * zoom avatar.
     * 
     * @param sourceImage
     *            the path of the picture which to be reduced.
     * @param width
     *            width of the zoom.
     * @param heigt
     *            height of the zoom.
     */
    private static BufferedImage zoom(BufferedImage sourceImage, int width,
            int height) {
        BufferedImage zoomImage = new BufferedImage(width, height, sourceImage
                .getType());
        Image image = sourceImage.getScaledInstance(width, height,
                Image.SCALE_SMOOTH);
        Graphics gc = zoomImage.getGraphics();
        gc.setColor(Color.WHITE);
        gc.drawImage(image, 0, 0, null);
        return zoomImage;
    }

    /**
     * get the format of the file.
     * 
     * @param file
     *            get the format from this file.
     * @return the format of the file.
     */
    public static String getImageFormatName(File file) throws IOException {
        String formatName = null;

        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> imageReader = ImageIO.getImageReaders(iis);
        if (imageReader.hasNext()) {
            ImageReader reader = imageReader.next();
            formatName = reader.getFormatName();
        }

        return formatName;
    }
}
