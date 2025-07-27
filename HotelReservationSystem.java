package reservationJDBCProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {
	private static final String url="jdbc:mysql://localhost:3306/hotel_db";
	private static final String userName="root";
	private static final String password="Rohith1234@#$/";
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			Connection con=DriverManager.getConnection(url,userName,password);
			while(true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYSTEM");
				Scanner sc=new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room numbers");
				System.out.println("4. Update Reservations");
				System.out.println("5. Delete Reservation");
				System.out.println("0. Exit");
				System.out.println("Choose an option:");
				int choice =sc.nextInt();
				switch(choice) {
				case 1:
					reserveRoom(con, sc);
					break;
				case 2:
					viewReservation(con);
					break;
				case 3:
					getRoomNumber(con, sc);
					break;
				case 4:
					updateReservation(con, sc);
					break;
				case 5:
					deleteReservation(con, sc);
					break;
				case 0:
					exit();
					sc.close();
					return;
				default:
					System.out.println("Invaid Choice, Try Again!");
				}	
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private static void reserveRoom(Connection con,Scanner sc) {
		try {
			System.out.println("Enter guest name:");
			String guestName=sc.next();
			System.out.println("Enter room number:");;
			int roomNumber=sc.nextInt();
			System.out.println("Enter Contact Number:");
			String contactNumber=sc.next();
			String query = "INSERT INTO reservation (guest_name, room_number, contact_number) VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";
			try(Statement st=con.createStatement()) {
				int rows=st.executeUpdate(query);
				if(rows>0) {
					System.out.println("Reservation Successful");
				} else {
					System.out.println("Reservation Failed.");
				}
			} 
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	private static void viewReservation(Connection con) throws SQLException {
		String query="select * from reservation;";
		try(Statement st=con.createStatement()){
			ResultSet res=st.executeQuery(query);
			System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while(res.next()) {
            	int id=res.getInt("reservation_ID");
            	String guestName=res.getString("guest_name");
            	int room=res.getInt("room_number");
            	String contact=res.getString("contact_number");
            	String date=res.getTimestamp("reservation_date").toString();
            	System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        id, guestName, room, contact, date);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
		}
	}
	private static void getRoomNumber(Connection con,Scanner sc) {
		try {
			System.out.println("Enter Reservation ID:");
			if (!sc.hasNextInt()) {
	            System.out.println("Invalid input! Reservation ID must be a number.");
	            sc.next();
	            return;
	        }
			int reservationID=sc.nextInt();
			System.out.println("Enter guest name:");
			String guest=sc.nextLine();
			guest=guest.replace("'", "''");
			String query="select room_number from reservation where reservation_ID="+reservationID+" and guest_name='"+guest+"'";
			try(Statement s=con.createStatement()){
				ResultSet res=s.executeQuery(query);
				if(res.next()) {
					int roomNumber=res.getInt("room_number");
					System.out.println("Room number for reservation id:"+reservationID+" and guest name:"+guest+"is:"+roomNumber);
				}else {
					System.out.println("Room number is not for the reservation id which was provided");
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	private static void updateReservation(Connection con,Scanner sc) {
		try {
			System.out.println("Enter reservation ID to update:");
			int reservationID=sc.nextInt();
			sc.nextLine();
			if(!reservationExists(con,reservationID)) {
				System.out.println("Reservation ID is not found to update");
				return;
			}
			System.out.println("Enter new guest name:");
			String newGuestName=sc.nextLine();
			System.out.println("Enter room number:");
			int roomNumber=sc.nextInt();
			System.out.println("Enter contact number:");
			String conatct=sc.next();
			String query="upadte reservation set guest_name='"+newGuestName+"', "+
			"room_number="+roomNumber+", "+
			"contact_number='"+conatct+"' "+
			"where reservation_ID="+reservationID;
			try(Statement s=con.createStatement()){
				int rows=s.executeUpdate(query);
				if(rows>1) {
					System.out.println("Reservation updates Successfully");
				} else{
					System.out.println("Reservation update failed");
				}
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	private static void deleteReservation(Connection con,Scanner sc) {
		try {
			System.out.println("Enter reservation ID to delete:");
			int reservationID=sc.nextInt();
			if(!reservationExists(con, reservationID)) {
				System.out.println("Reservation ID is not existed");
				return;
			}
			String query="delete from reservation where reservationID="+reservationID;
			try(Statement s=con.createStatement()){
				int rows=s.executeUpdate(query);
				if(rows>1) {
					System.out.println("Reservation is deleted Successfully");
				} else {
					System.out.println("Reservation deletion is failed");
				}
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	private static boolean reservationExists(Connection con, int reservationID) {
		try {
			String query="select reservation_ID from reservation where reservation_ID="+reservationID;
			try(Statement s=con.createStatement()) {
				ResultSet res=s.executeQuery(query);
				return res.next();
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	public static void exit() throws InterruptedException {
		System.out.println("Exiting System");
		int i=5;
		while(i!=0) {
			System.out.println(".");
			Thread.sleep(1000);
			i--;
		}
		System.out.println();
		System.out.println("ThankYou For Using Hotel Reservation System!!!");
	}
}
