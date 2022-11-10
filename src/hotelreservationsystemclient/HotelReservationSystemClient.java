/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationsystemclient;

import ws.client.car.CarWebService;
import ws.client.car.CarWebService_Service;

/**
 *
 * @author seantan
 */
public class HotelReservationSystemClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CarWebService_Service carWebService_Service = new CarWebService_Service();
        CarWebService carWebServicePort = carWebService_Service.getCarWebServicePort();
        System.out.println(carWebServicePort.hello("WEB SERVICE STUFF"));
    }

}
