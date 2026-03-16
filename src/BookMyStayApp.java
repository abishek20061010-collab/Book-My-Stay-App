import java.util.*;

/**
 * Book My Stay Application
 * Combined implementation of Use Cases 1–6
 *
 * UC1 – Application startup
 * UC2 – Room domain modeling using abstraction
 * UC3 – Centralized inventory using HashMap
 * UC4 – Room search service
 * UC5 – Booking request queue (FIFO)
 * UC6 – Room allocation with unique IDs and double booking prevention
 *
 * @author Student
 * @version 6.1
 */
public class BookMyStayApp{

    public static void main(String[] args) {

        // ===== UC1: Application Entry =====
        System.out.println("=======================================");
        System.out.println("       Book My Stay - Hotel System");
        System.out.println("              Version 6.1");
        System.out.println("=======================================\n");


        // ===== UC2: Room Objects =====
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Room[] rooms = {single, doubleRoom, suite};


        // ===== UC3: Centralized Inventory =====
        RoomInventory inventory = new RoomInventory();


        // ===== UC4: Room Search =====
        RoomSearchService searchService = new RoomSearchService(inventory);

        System.out.println("Available Rooms:\n");
        searchService.searchAvailableRooms(rooms);


        // ===== UC5: Booking Request Queue =====
        Queue<Reservation> requestQueue = new LinkedList<>();

        requestQueue.offer(new Reservation("Alice", "Single Room"));
        requestQueue.offer(new Reservation("Bob", "Double Room"));
        requestQueue.offer(new Reservation("Charlie", "Suite Room"));
        requestQueue.offer(new Reservation("David", "Single Room"));

        System.out.println("\nBooking Requests Added to Queue\n");


        // ===== UC6: Room Allocation =====
        RoomAllocationService allocationService =
                new RoomAllocationService(inventory);

        allocationService.processRequests(requestQueue);

        allocationService.displayAllocations();

        System.out.println("\nUpdated Inventory:");
        inventory.displayInventory();

        System.out.println("\nSystem Execution Completed.");
    }
}



/* =====================================================
   UC2: ROOM DOMAIN MODEL
   ===================================================== */

/**
 * Abstract Room class
 */
abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq.ft");
        System.out.println("Price     : $" + price);
    }
}

class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 200, 100);
    }
}

class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 350, 180);
    }
}

class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 600, 350);
    }
}



/* =====================================================
   UC3: CENTRALIZED INVENTORY
   ===================================================== */

class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int newCount) {
        inventory.put(roomType, newCount);
    }

    public void displayInventory() {

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> Available: " + entry.getValue());
        }
    }
}



/* =====================================================
   UC4: ROOM SEARCH SERVICE
   ===================================================== */

class RoomSearchService {

    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(Room[] rooms) {

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getRoomType());

            if (available > 0) {

                room.displayRoomDetails();
                System.out.println("Available Rooms: " + available);
                System.out.println("-----------------------------");
            }
        }
    }
}



/* =====================================================
   UC5: BOOKING REQUEST QUEUE
   ===================================================== */

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}



/* =====================================================
   UC6: ROOM ALLOCATION SERVICE
   ===================================================== */

class RoomAllocationService {

    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms;
    private int roomCounter = 1;

    public RoomAllocationService(RoomInventory inventory) {

        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
    }

    public void processRequests(Queue<Reservation> queue) {

        while (!queue.isEmpty()) {

            Reservation reservation = queue.poll();
            String roomType = reservation.getRoomType();

            int available = inventory.getAvailability(roomType);

            if (available > 0) {

                String roomId = generateRoomId(roomType);

                allocatedRooms
                        .computeIfAbsent(roomType, k -> new HashSet<>())
                        .add(roomId);

                inventory.updateAvailability(roomType, available - 1);

                System.out.println(
                        "Booking Confirmed: "
                                + reservation.getGuestName()
                                + " | " + roomType
                                + " | Room ID: " + roomId
                );

            } else {

                System.out.println(
                        "Booking Failed: "
                                + reservation.getGuestName()
                                + " | No " + roomType + " available"
                );
            }
        }
    }

    private String generateRoomId(String roomType) {

        String prefix = roomType.replaceAll(" ", "").substring(0, 2).toUpperCase();
        return prefix + "-" + (roomCounter++);
    }

    public void displayAllocations() {

        System.out.println("\nFinal Allocations:");

        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {

            System.out.println("Room Type: " + entry.getKey());
            System.out.println("Room IDs : " + entry.getValue());
        }
    }
}