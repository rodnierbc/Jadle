package dao;

import org.junit.After;
import org.junit.Before;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import models.Restaurant;
import models.Foodtype;
import org.junit.Test;
import java.util.Arrays;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {
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
    public void getAllFoodtypesForARestaurantReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype  = new Foodtype("Bar Food");
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodType(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype}; //oh hi what is this?

        assertEquals(restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()), Arrays.asList(foodtypes));
    }
    public Restaurant setupRestaurant(){
        return new Restaurant("La Provance", "Portland","97237","323422333", "laprovance.com","laprovance@gmail.com");
    }
    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype foodtype  = new Foodtype("Seafood");
        foodtypeDao.add(foodtype);
        Foodtype foodtype1 = new Foodtype("french food");
        foodtypeDao.add(foodtype1);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);



        foodtypeDao.addFoodTypeToRestaurant(foodtype, testRestaurant);
        foodtypeDao.addFoodTypeToRestaurant(foodtype1, testRestaurant);

        foodtypeDao.deleteById(testRestaurant.getId());
        assertEquals(0, foodtypeDao.getAllRestaurantsForAFoodtype(testRestaurant.getId()).size());
    }

}