package endpoints;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import tools.*;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/decode")
public class QRResource {
    @POST
    @Path("/pdf")
    @Consumes("application/pdf")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response readPDF(InputStream input){
        int errorCode = 0;
        List<String> data = new ArrayList();
        String message = "Output is valid.";
        int httpStatus = 200;

        List<BufferedImage> bufferedImages = null;
        try {
            bufferedImages = PDFConverter.pdfToImage(input);
        } catch (Throwable e){
            errorCode = 1;
            message = "PDFConverter error: " + e.getMessage();
            httpStatus = 500;
        }

        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        String charset = "UTF-8";

        if (bufferedImages != null){
            for (BufferedImage bufferedImage : bufferedImages){
                String result = "";

                try {
                    result = QRReader.readQR(bufferedImage, charset, hashMap);
                } catch (Throwable e){
                    if (e.getMessage() != null){
                        errorCode = 2;
                        message = "QRReader exception: " + e.getMessage();
                        httpStatus = 202;
                    } else {
                        message = "Source file has empty pages.";
                    }
                }

                //result = StringEncoder.encode(result);

                if (result.length() > 0) {
                    data.add(result);
                }
            }
        } else {
            errorCode = 3;
            message = "Image buffer is null.";
            httpStatus = 422;
        }

        JSONBean jsonBean = new JSONBean(errorCode, data, message);

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(jsonBean);

        return builder.build();
    }

    @POST
    @Path("/image")
    @Consumes({"image/jpg", "image/png", "image/gif", "image/svg+xml", "image/webp", "image/avif", "image/apng"})
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response readImage(InputStream input){
        int errorCode = 0;
        List<String> data = new ArrayList();
        String message = "Output is valid.";
        int httpStatus = 200;

        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        String charset = "UTF-8";

        String result = "";
        try {
            BufferedInputStream bufferStream = new BufferedInputStream(input);
            BufferedImage bufferedImage = ImageIO.read(bufferStream);
            result = QRReader.readQR(bufferedImage, charset, hashMap);
            //result = StringEncoder.encode(result);
            data.add(result);
        } catch (Throwable e){
            errorCode = 2;
            message = "Source file is empty. Check if there is a QRReader exception: " + e.getMessage();
            httpStatus = 204;
        }

        JSONBean jsonBean = new JSONBean(errorCode, data, message);

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(jsonBean).encoding("Cp1251");
        return builder.build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Produces("image/png")
    public Response createQR(CreateRequest request){
        String charset = "UTF-8";
        int httpStatus = 200;

        byte[] byteResponse = null;

        try {
            byteResponse = QRReader.createQR(request.data, charset, request.size);
        } catch (Throwable e){
            httpStatus = 500;
        }

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(byteResponse);
        return builder.build();
    }

    @POST
    @Path("/size")
    @Consumes({"image/jpg", "image/png", "image/gif", "image/svg+xml", "image/webp", "image/avif", "image/apng"})
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response readImageSize(InputStream input){
        int errorCode = 0;
        List<String> data = new ArrayList();
        String message = "Output is valid.";
        int httpStatus = 200;

        try {
            BufferedInputStream bufferStream = new BufferedInputStream(input);
            BufferedImage bufferedImage = ImageIO.read(bufferStream);
            int size = PDFConverter.measureLength(bufferedImage);
            data.add("Buffered image size: " + size + " bytes");
        } catch (Throwable e){
            errorCode = 2;
            message = "Source file is empty. Check if there is a QRReader exception: " + e.getMessage();
            httpStatus = 204;
        }

        JSONBean jsonBean = new JSONBean(errorCode, data, message);

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(jsonBean).encoding("Cp1251");
        return builder.build();
    }

    @POST
    @Path("/pagessize")
    @Consumes("application/pdf")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response readPDFSize(InputStream input){
        int errorCode = 0;
        List<String> data = new ArrayList();
        String message = "Output is valid.";
        int httpStatus = 200;

        List<BufferedImage> bufferedImages = null;
        try {
            bufferedImages = PDFConverter.pdfToImage(input);
        } catch (Throwable e){
            errorCode = 1;
            message = "PDFConverter error: " + e.getMessage();
            httpStatus = 500;
        }

        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        String charset = "UTF-8";

        if (bufferedImages != null){
            for (BufferedImage bufferedImage : bufferedImages){
                try {
                    int size = PDFConverter.measureLength(bufferedImage);
                    data.add("Page size: " + size + " bytes");
                } catch (IOException e){
                    errorCode = 4;
                    message = "Failed to measure page length";
                    httpStatus = 500;
                }
            }
        } else {
            errorCode = 3;
            message = "Image buffer is null.";
            httpStatus = 422;
        }

        JSONBean jsonBean = new JSONBean(errorCode, data, message);

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(jsonBean);

        return builder.build();
    }
}