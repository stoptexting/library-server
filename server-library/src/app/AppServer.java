package app;

import java.util.HashMap;
import java.util.Map;

import library.servers.Serveur;
import library.services.Service;

@SuppressWarnings("unchecked")
public class AppServer {

	private static Map<String, Integer> PORTS;
	private static Class<? extends Service> reservation, emprunt, retour;
	static {
		AppServer.PORTS = new HashMap<>();
		try {
			AppServer.reservation = (Class<? extends Service>) Class.forName("library.services.ServiceReservation");
			AppServer.emprunt = (Class<? extends Service>) Class.forName("library.services.ServiceEmprunt");
			//AppServer.retour = (Class<? extends Service>) Class.forName("library.services.ServiceRetour");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		getPorts(args);
        new Thread(new Serveur(port("Reservation"), AppServer.reservation)).start();
	}
	
	
	
	public static Integer port(String port_name) {
		return AppServer.PORTS.get(port_name.toUpperCase());
	}
	
	
	public static void getPorts(String[] args) {
		int ePort = 0, rsPort = 0, rtPort = 0;
		
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-e")) {
                i++;
                if (i < args.length) {
                    try {
                        ePort = Integer.parseInt(args[i]);
                        if (ePort < 0 || ePort > 65535) {
                            System.err.println("Invalid port number for -e flag: " + args[i]);
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid port number for -e flag: " + args[i]);
                        System.exit(1);
                    }
                } else {
                    System.err.println("-e flag requires a port number argument.");
                    System.exit(1);
                }
            } else if (args[i].equals("-rs")) {
                i++;
                if (i < args.length) {
                    try {
                        rsPort = Integer.parseInt(args[i]);
                        if (rsPort < 0 || rsPort > 65535) {
                            System.err.println("Invalid port number for -rs flag: " + args[i]);
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid port number for -rs flag: " + args[i]);
                        System.exit(1);
                    }
                } else {
                    System.err.println("-rs flag requires a port number argument.");
                    System.exit(1);
                }
            } else if (args[i].equals("-rt")) {
                i++;
                if (i < args.length) {
                    try {
                        rtPort = Integer.parseInt(args[i]);
                        if (rtPort < 0 || rtPort > 65535) {
                            System.err.println("Invalid port number for -rt flag: " + args[i]);
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid port number for -rt flag: " + args[i]);
                        System.exit(1);
                    }
                } else {
                    System.err.println("-rt flag requires a port number argument.");
                    System.exit(1);
                }
            }
        }

        // Verify that all three flags were provided with valid port numbers
        if (ePort == 0 || rsPort == 0 || rtPort == 0) {
            System.err.println("Please provide valid port numbers for all three flags: -e, -rs, -rt");
            System.exit(1);
        }
        
        AppServer.PORTS.put("EMPRUNT", ePort);
        AppServer.PORTS.put("RESERVATION", rsPort);
        AppServer.PORTS.put("RETOUR", rtPort);
        
        return;
	}
}
