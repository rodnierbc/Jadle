package dao;

import org.junit.After;
import org.junit.Before;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import models.Restaurant;
import models.Foodtype;
import org.junit.Test;

import static org.junit.Assert.*;

public class Sql2oFoodtypeDaoTest {

    private Connection conn;
    private Sql2oFoodtypeDao foodtypeDao;
    private Sql2oRestaurantDao restaurantDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        conn = sql2o.open();

    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }
    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {

        Restaurant testRestaurant = setupRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setupNewFoodtype();

        foodtypeDao.add(testFoodtype);

        foodtypeDao.addFoodTypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodTypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    private Foodtype setupNewFoodtype() {
        return new Foodtype("French food");
    }

    public Restaurant setupRestaurant(){
        return new Restaurant("La Provance", "Portland","97237","323422333", "laprovance.com","laprovance@gmail.com");
    }
    public Restaurant setupAltRestaurant(){
        return new Restaurant("La Provance1", "Portland1","972371","3234223331", "laprovance1.com","laprovance1@gmail.com");
    }
    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodType(altRestaurant, testFoodtype);

        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()).size());
    }




}