import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.example.javafx.Controller.RouteFinderController;
import org.example.javafx.Models.Station;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RouteFinderControllerTest {

    private RouteFinderController controller;

    @BeforeAll
    void initJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX Platform failed to start.");
    }

    @BeforeEach
    void setUp() {
        controller = new RouteFinderController();

        // Manually inject dummy components
        controller.routeOutput = new ListView<>();
        controller.dfsAllListView = new ListView<>();
        controller.avoidView = new ListView<>();
        controller.waypointView = new ListView<>();
        controller.mapImage = new ImageView();
        controller.avoidStation = new MenuButton("Select Station");
        controller.waypointStation = new MenuButton("Select Station");
        controller.startStation = new MenuButton("Start");
        controller.endStation = new MenuButton("End");
    }

    @Test
    void testOutputMessage_displaysCorrectly() {
        Platform.runLater(() -> {
            controller.outputMessage("Test message");
            List<String> items = controller.routeOutput.getItems();
            assertEquals(1, items.size());
            assertEquals("Test message", items.get(0));
        });
    }

    @Test
    void testDisplayRoute_nullRouteShowsError() {
        Platform.runLater(() -> {
            controller.displayRoute("BFS", null);
            List<String> items = controller.routeOutput.getItems();
            assertEquals(1, items.size());
            assertEquals("❌ No route found.", items.get(0));
        });
    }

    @Test
    void testClearMap_resetsAllFields() {
        Platform.runLater(() -> {
            controller.avoidView.getItems().add("Dummy");
            controller.waypointView.getItems().add("Dummy");
            controller.routeOutput.getItems().add("Dummy route");

            controller.selectedStartStation = new Station("Start",0.0,0.0);
            controller.selectedEndStation = new Station("End",0.0,0.0);

            controller.clearMap(null);

            assertTrue(controller.routeOutput.getItems().isEmpty());
            assertTrue(controller.avoidView.getItems().isEmpty());
            assertTrue(controller.waypointView.getItems().isEmpty());
            assertNull(controller.selectedStartStation);
            assertNull(controller.selectedEndStation);
            assertEquals("Starting Station", controller.startStation.getText());
        });
    }

    @Test
    void testAddAvoid_nonExistingStationDoesNothing() {
        Platform.runLater(() -> {
            controller.avoidStation.setText("NonExisting");
            controller.addAvoid(null);
            assertTrue(controller.avoidStations.isEmpty());
            assertTrue(controller.avoidView.getItems().isEmpty());
        });
    }

    @Test
    void testWaypointSearch_requiresTwoWaypoints() {
        Platform.runLater(() -> {
            controller.outputMessage("Initial");
            controller.waypointSearch(null); // Should prompt warning
            List<String> items = controller.routeOutput.getItems();
            assertEquals("⚠️ Add at least 2 waypoints.", items.get(0));
        });
    }
}