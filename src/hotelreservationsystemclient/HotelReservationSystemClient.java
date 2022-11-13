/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationsystemclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.car.CarWebService;
import ws.client.car.CarWebService_Service;
import ws.client.car.CarsNotFoundException_Exception;
import ws.client.car.Category;
import ws.client.creditcard.PaymentModeEnum;
import ws.client.creditcard.CreditCard;
import ws.client.creditcard.CreditCardWebService;
import ws.client.creditcard.CreditCardWebService_Service;
import ws.client.customer.Customer;
import ws.client.customer.CustomerWebService;
import ws.client.customer.CustomerWebService_Service;
import ws.client.model.Model;
import ws.client.model.ModelNotFoundException_Exception;
import ws.client.model.ModelWebService;
import ws.client.model.ModelWebService_Service;
import ws.client.partner.InvalidLoginCredentialException_Exception;
import ws.client.partner.Partner;
import ws.client.partner.PartnerWebService;
import ws.client.partner.PartnerWebService_Service;
import ws.client.reservation.Reservation;
import ws.client.reservation.ReservationNotFoundException_Exception;
import ws.client.reservation.ReservationWebService;
import ws.client.reservation.ReservationWebService_Service;

/**
 *
 * @author seantan
 */
public class HotelReservationSystemClient {

    /**
     * @param args the command line arguments
     */
    private static Partner currentPartner;
    private static PartnerWebService partnerWebServicePort;
    private static CarWebService carWebServicePort;
    private static ReservationWebService reservationWebServicePort;
    private static CreditCardWebService creditCardWebServicePort;
    private static ModelWebService modelWebServicePort;
    private static CustomerWebService customerWebServicePort;

    public static void main(String[] args) {
        currentPartner = null;

        PartnerWebService_Service partnerWebService_Service = new PartnerWebService_Service();
        partnerWebServicePort = partnerWebService_Service.getPartnerWebServicePort();

        CarWebService_Service carWebService_Service = new CarWebService_Service();
        carWebServicePort = carWebService_Service.getCarWebServicePort();

        ReservationWebService_Service reservationWebService_Service = new ReservationWebService_Service();
        reservationWebServicePort = reservationWebService_Service.getReservationWebServicePort();

        CreditCardWebService_Service creditCardWebService_Service = new CreditCardWebService_Service();
        creditCardWebServicePort = creditCardWebService_Service.getCreditCardWebServicePort();

        ModelWebService_Service modelWebService_Service = new ModelWebService_Service();
        modelWebServicePort = modelWebService_Service.getModelWebServicePort();

        CustomerWebService_Service customerWebService_Service = new CustomerWebService_Service();
        customerWebServicePort = customerWebService_Service.getCustomerWebServicePort();

        runApp();

    }

    public static void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to CaRMS Reservation System ***\n");

            if (currentPartner != null) {
                System.out.println("You are logged in as " + currentPartner.getPartner() + "\n");

                System.out.println("1: Search & Reserve Car");
                System.out.println("2: View All my Reservations");
                System.out.println("3: Logout\n");
                response = 0;

                while (response < 1 || response > 4) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        doSearchCar();
                    } else if (response == 2) {
                        doViewReservations();
                    } else if (response == 3) {
                        currentPartner = null;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

//                if (response == 4) {
//                    break;
//                }
            } else {
                System.out.println("1: Login");
                System.out.println("2: Search Car");
                System.out.println("3: Exit\n");
                response = 0;

                while (response < 1 || response > 3) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        if (currentPartner == null) {
                            try {
                                doLogin();
                            } catch (InvalidLoginCredentialException_Exception ex) {
                                System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                            }
                        } else {
                            System.out.println("You are already login as " + currentPartner.getPartner() + "\n");
                        }
                    } else if (response == 2) {
                        doSearchCar();
                    } else if (response == 3) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                if (response == 3) {
                    break;
                }
            }

        }
    }

    private static void doLogin() throws InvalidLoginCredentialException_Exception {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.println("*** CaRMS Reservation System :: Partner Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            try {
                currentPartner = partnerWebServicePort.login(email, password);
                System.out.println("Login successful as " + currentPartner.getPartner() + "!\n");
            } catch (InvalidLoginCredentialException_Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Invalid login credentials");
        }
    }

    private static void doSearchCar() {
        try {
            Scanner scanner = new Scanner(System.in);
            Long categoryId;
            Long modelId;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Long pickupOutletId;
            Long returnOutletId;
            Date pickupDate = inputDateFormat.parse("06/12/2022 02:00 PM");
            Date returnDate = inputDateFormat.parse("10/12/2022 12:00 PM");
//            Date pickupDate;
//            Date returnDate;
            Long carId;

            System.out.println("*** CaRMS Reservation System :: Search Car ***\n");

            System.out.print("Enter pickup outlet> ");
            while (true) {
                System.out.print("Select Outlet (1: Outlet A, 2: Outlet B, 3: Outlet C)> ");
                pickupOutletId = scanner.nextLong();
                scanner.nextLine();

                if (pickupOutletId < 1 && pickupOutletId > 3) {
                    System.out.println("Invalid option, please try again!\n");
                } else {
                    break;
                }
            }

            System.out.print("Enter return outlet> ");
            while (true) {
                System.out.print("Select Outlet (1: Outlet A, 2: Outlet B, 3: Outlet C)> ");
                returnOutletId = scanner.nextLong();
                scanner.nextLine();

                if (returnOutletId < 1 && returnOutletId > 3) {
                    System.out.println("Invalid option, please try again!\n");
                } else {
                    break;
                }
            }
//            System.out.print("Enter Pickup Date (dd/mm/yyyy hh:mm a)> ");
//            pickupDate = inputDateFormat.parse(scanner.nextLine().trim());
//
//            System.out.print("Enter Return Date (dd/mm/yyyy hh:mm a)> ");
//            returnDate = inputDateFormat.parse(scanner.nextLine().trim());

            List<Category> categories = carWebServicePort.searchCars(pickupOutletId, returnOutletId, convertDate(pickupDate), convertDate(returnDate));

            System.out.printf("%-22s%-22s%-22s%-22s\n", "Category ID", "Category", "Available?", "Rental Fee");

            for (Category category : categories) {

                System.out.printf("%-22s%-22s%-22s%-22s\n", category.getCategoryId(), category.getCategory(), category.isAvailable(), category.getRentalFee());
            }

            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            Integer response = scanner.nextInt();

            if (response == 1) {
                if (currentPartner != null) {

                    System.out.println("*** CaRMS Reservation System :: Enter Reservation Details ***\n");

                    System.out.print("Select Category Id> ");
                    categoryId = scanner.nextLong();
                    scanner.nextLine();

                    System.out.println("Enter model> ");
                    List<Model> models = null;
                    try {
                        models = modelWebServicePort.retrieveAllModelsSorted();
                    } catch (ModelNotFoundException_Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    while (true) {
                        System.out.print("Select Model Type ID: ");
                        for (Model model : models) {
                            System.out.println(model.getModelId() + ": " + model.getMake() + " " + model.getModel());
                        }
                        modelId = scanner.nextLong();
                        scanner.nextLine();

                        if (modelId < 1 && modelId > models.size()) {
                            System.out.println("Invalid option, please try again!\n");
                        } else {
                            break;
                        }
                    }

                    String fullName = "";
                    String email = "";
                    String password = "";
                    PaymentModeEnum paymentMode;
                    String creditCardNumber;
                    Boolean paid;
                    Long newCustomerId = null;
                    Customer currentCustomer = null;

                    System.out.println("*** CaRMS Reservation System :: Enter Customer Details ***\n");
                    System.out.print("Enter full name> ");
                    fullName = scanner.nextLine().trim();
                    System.out.print("Enter email> ");
                    email = scanner.nextLine().trim();
                    System.out.print("Enter password> ");
                    password = scanner.nextLine().trim();

                    if (fullName.length() > 0 && email.length() > 0 && password.length() > 0) {
//                        currentCustomer = new Customer(fullName, email, password);
                        currentCustomer = new Customer();
                        currentCustomer.setFullName(fullName);
                        currentCustomer.setEmail(email);
                        currentCustomer.setPassword(password);
                        newCustomerId = customerWebServicePort.createNewCustomer(currentCustomer);
                        System.out.println("Registration successful with customer ID " + newCustomerId + "!\n");
                    }

                    System.out.println("*** CaRMS Reservation System :: Enter Payment Details ***\n");

                    while (true) {
                        System.out.print("Select Payment Time (1: Pay now, 2: Pay during pickup)> ");
                        Integer paymentTimeInt = scanner.nextInt();

                        if (paymentTimeInt == 1) {
                            paid = true;
                            break;
                        } else if (paymentTimeInt == 2) {
                            paid = false;
                            break;
                        } else {
                            System.out.println("Invalid option, please try again!\n");
                        }
                    }

                    while (true) {
                        System.out.print("Select Payment Mode (1: VISA, 2: MasterCard, 3: AMEX)> ");
                        Integer paymentModeInt = scanner.nextInt();

                        if (paymentModeInt >= 1 && paymentModeInt <= 3) {
                            paymentMode = PaymentModeEnum.values()[paymentModeInt - 1];
                            break;
                        } else {
                            System.out.println("Invalid option, please try again!\n");
                        }
                    }

                    scanner.nextLine();
                    System.out.print("Enter Credit Card Number> ");
                    creditCardNumber = scanner.nextLine().trim();

                    CreditCard creditCard = new CreditCard();
                    creditCard.setPaymentMode(paymentMode);
                    creditCard.setCreditCardNumber(creditCardNumber);

                    creditCardWebServicePort.createNewCreditCard(creditCard, newCustomerId);
                    System.out.println("New credit card created: " + creditCardNumber);

                    Reservation reservation = new Reservation();
                    reservation.setPaid(paid);
                    reservation.setPickupDate(convertDate(pickupDate));
                    reservation.setReturnDate(convertDate(returnDate));

                    Long reservationId = reservationWebServicePort.createNewReservation(reservation, newCustomerId, categoryId, pickupOutletId, returnOutletId, modelId, currentPartner.getPartnerId());

                    System.out.println("Reservation of car completed successfully!: " + reservationId + "\n");
                } else {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (CarsNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void doViewReservations() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** CaRMS Reservation System :: List of Reservations ***\n");

        List<Reservation> reservations = reservationWebServicePort.retrievePartnerReservations(currentPartner.getPartnerId());

        if (reservations == null) {
            System.out.println("No reservations made!");
        } else {
            System.out.printf("%-22s%-22s%-22s%-30s%-22s%-30s\n", "Reservation Id", "Rental Fee", "Pickup Outlet", "Pickup Date", "Return Outlet", "Return Date");

            for (Reservation reservation : reservations) {
                System.out.printf("%-22s%-22s%-22s%-30s%-22s%-30s\n", reservation.getReservationId(), reservation.getRentalFee(), reservation.getPickupOutlet().getAddress(), reservation.getPickupDate(), reservation.getReturnOutlet().getAddress(), reservation.getReturnDate());
            }
        }

        System.out.println("------------------------");
        System.out.println("1: View Reservation Details");
        System.out.println("2: Back\n");
        System.out.print("> ");
        Integer response1 = scanner.nextInt();

        try {
            if (response1 == 1) {
                System.out.print("Select Reservation Id> ");
                Long reservationId = scanner.nextLong();
                scanner.nextLine();

                System.out.println("*** CaRMS Reservation System :: Reservation Details ***\n");

                Reservation reservation = reservationWebServicePort.retrieveReservationById(reservationId);

                System.out.println("Reservation Id: " + reservation.getReservationId());
                System.out.println("Rental Fee: " + reservation.getRentalFee());
                System.out.println("Pickup Outlet: " + reservation.getPickupOutlet().getAddress());
                System.out.println("Pickup Date: " + reservation.getPickupDate());
                System.out.println("Return Outlet: " + reservation.getReturnOutlet().getAddress());
                System.out.println("Return Date: " + reservation.getReturnDate());

                System.out.println("------------------------");
                System.out.println("1: Cancel Reservation");
                System.out.println("2: Back\n");
                System.out.print("> ");
                Integer response2 = scanner.nextInt();
                scanner.nextLine();

                if (response2 == 1) {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    System.out.print("Enter Cancellation Date (dd/mm/yyyy hh:mm a)> ");
                    Date cancellationDate = inputDateFormat.parse(scanner.nextLine().trim());

                    reservationWebServicePort.cancelReservation(reservationId, convertDate(cancellationDate));

                    System.out.println("Reservation successfully cancelled");

                }
            }
        } catch (ReservationNotFoundException_Exception ex) {
            System.out.println(ex.getMessage());
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private static XMLGregorianCalendar convertDate(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

}
