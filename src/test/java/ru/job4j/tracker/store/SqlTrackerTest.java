package ru.job4j.tracker.store;

import org.junit.*;
import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SqlTrackerTest {

    static Connection connection;

    @BeforeClass
    public static void initConnection() {
        try (InputStream in = SqlTrackerTest.class.getClassLoader().getResourceAsStream("test.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @After
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from items")) {
            statement.execute();
        }
    }

    @Test
    public void whenSaveItemAndFindByGeneratedIdThenMustBeTheSame() {
        SqlTracker tracker = new SqlTracker(connection);
        Item item = new Item("item");
        tracker.add(item);
        assertThat(tracker.findById(item.getId()), is(item));
    }

    @Test
    public void whenSaveItemAndThenReplaceAnotherItemIdMustRemainSame() {
        SqlTracker tracker = new SqlTracker(connection);
        Item first = new Item("First");
        tracker.add(first);
        Item second = new Item("Second");
        tracker.replace(first.getId(), second);
        String expected = tracker.findById(first.getId()).getName();
        String actual = second.getName();
        assertThat(expected, is(actual));
    }

    @Test
    public void whenSaveItemAndDeleteThisItem() {
        SqlTracker tracker = new SqlTracker(connection);
        Item first = new Item("First");
        tracker.add(first);
        tracker.delete(first.getId());
        Item it = tracker.findById(first.getId());
        assertNull(it);
    }

    @Test
    public void whenAddThreeItemAndGetListOfTheSameItems() {
        SqlTracker tracker = new SqlTracker(connection);
        Item first = new Item("First");
        tracker.add(first);
        Item second = new Item("Second");
        tracker.add(second);
        Item third = new Item("Third");
        tracker.add(third);
        List<Item> list = tracker.findAll();
        Iterator<Item> it = list.iterator();
        assertThat(it.next(), is(first));
        assertThat(it.next(), is(second));
        assertThat(it.next(), is(third));
    }

    @Test
    public void whenAddItemAndfindByNameThisItem() {
        SqlTracker tracker = new SqlTracker(connection);
        Item first = new Item("First");
        tracker.add(first);
        List<Item> list = tracker.findByName("st");
        Iterator<Item> it = list.iterator();
        assertThat(it.next(), is(first));
    }

    @Test
    public void whenAddItemAndfindByIdThisItem() {
        SqlTracker tracker = new SqlTracker(connection);
        Item first = new Item("First");
        tracker.add(first);
        Item expected = tracker.findById(first.getId());
        assertThat(expected.getName(), is("First"));
    }
}