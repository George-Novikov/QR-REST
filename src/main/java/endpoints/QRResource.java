package endpoints;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.pdfbox.pdmodel.PDDocument;
import tools.*;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
        List<Object> data = new ArrayList();
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
                    result = new QRReader().readQR(bufferedImage);
                } catch (Throwable e){
                    if (e.getMessage() != null){
                        errorCode = 2;
                        message = "QRReader exception: " + e.getMessage();
                        httpStatus = 202;
                    } else {
                        message = "Source file has empty pages.";
                    }
                }

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
        List<Object> data = new ArrayList();
        String message = "Output is valid.";
        int httpStatus = 200;

        String result = "";
        try {
            BufferedInputStream bufferStream = new BufferedInputStream(input);
            BufferedImage bufferedImage = ImageIO.read(bufferStream);
            result = new QRReader().readQR(bufferedImage);
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
    @Path("/map")
    @Consumes("application/pdf")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response mapDocument(InputStream input){
        int errorCode = 0;
        List<Object> data = new ArrayList();
        String message = "Output is valid.";
        int httpStatus = 200;

        try {
            data = PDFConverter.mapSubdocuments(input);
        } catch (Throwable e){
            errorCode = 1;
            message = "PDFConverter.mapSubdocuments() error: " + e.getMessage();
            httpStatus = 500;
        }

        JSONBean jsonBean = new JSONBean(errorCode, data, message);

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(jsonBean);

        return builder.build();
    }

    @POST
    @Path("/getsubdoc")
    @Consumes("application/pdf")
    @Produces("application/pdf")
    public Response getSubdocument(InputStream input,
                                   @QueryParam("start") int start,
                                   @QueryParam("end") int end){
        int httpStatus = 200;
        PDDocument subdocument = null;
        byte[] output = null;

        try {
            subdocument = PDFConverter.getSubdocument(input, start, end);

            if (subdocument != null){
                output = PDFConverter.toByteArray(subdocument);
                subdocument.close();
            } else {
                PDDocument emptyDoc = PDFConverter.createEmpty();
                output = PDFConverter.toByteArray(subdocument);
                emptyDoc.close();
                httpStatus = 204;
            }
        } catch (Throwable e){}

        Response.ResponseBuilder builder = Response.status(httpStatus).entity(output);

        return builder.build();
    }
}