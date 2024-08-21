package RestServer.services;

import RestServer.beans.robots.MyResponse;
import RestServer.beans.robots.RobotBean;
import RestServer.beans.robots.Robots;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("robots")
public class RobotService {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getRobots(){
        return Response.ok(Robots.getInstance()).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addRobot(RobotBean robotBean) {
        MyResponse result = Robots.getInstance().add(robotBean);
        return result != null ? Response.ok(result).build() : Response.status(Response.Status.CONFLICT).build();
    }

    @Path("get")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getRobotsList(){
        ArrayList<RobotBean> robotsList = Robots.getInstance().getRobotsList();
        return Response.ok(robotsList).build();
    }

    @Path("get/{id}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getById(@PathParam("id") int id){
        RobotBean robotBean = Robots.getInstance().getById(id);
        if (robotBean == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(robotBean).build();
    }

    @Path("remove/{id}")
    @DELETE
    @Produces({"application/json", "application/xml"})
    public Response deleteById(@PathParam("id") int id){
        RobotBean robotBean = Robots.getInstance().deleteById(id);
        if(robotBean !=null)
            return Response.ok(robotBean).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }

}
