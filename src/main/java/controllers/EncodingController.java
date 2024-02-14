package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.CreateRequest;
import services.QRService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/encode")
public class EncodingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncodingController.class);

    @Inject
    private QRService qrReader;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Produces("image/png")
    public Response createQR(CreateRequest request){
        try {
            byte[] byteResponse = qrReader.createQR(request.getData(), request.getSize());
            return Response.ok(byteResponse).build();
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
