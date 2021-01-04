package com.greg.go4lunch;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.greg.go4lunch.model.LikedRestaurant;
import com.greg.go4lunch.model.Restaurant;
import com.greg.go4lunch.model.Workmate;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private final String uid = "gHlpruHkhfF";
    private final String picture = "https://www.hdwallpaper.nu/wp-content/uploads/2015/02/darth-vader-digital-art-hd-wallpaper-1920x1200-7532.jpg";
    private final String name = "Dark Vador";
    private final String email = "darkvador@gmail.com";
    private final String pickedRestaurant = "Backyard Bowls";
    private final String idPickedRestaurant = "ChIJnW5MibbHwoARMV_994-EPyo";
    private final String addressRestaurant = "801 D, S Hope St, Los Angeles, CA 90017, USA";
    private final boolean joining = true;

    private final boolean favorite = true;

    private final String idRestaurant = "ChIJnW5MibbHwoARMV_994-EPyo";
    private final String distanceFromUser = "23m";
    private final String phoneNumber = "+1 323-977-2255";
    private final String website = "http://www.backyardbowls.com/";
    private final PhotoMetadata restaurantPicture = null;
    private final LatLng latLng = new LatLng(34.0472732, -118.2604486);
    private final int joiningNumber = 9;
    private final int openingHour = 23;
    private final float rating = (float) 4.6;

    @Test
    public void workmate_withNoRestaurantChosen(){
        Workmate workmateWithNoRestaurantChosen = new Workmate(uid, picture, name, email, null,
                null, null, false);
        assertEquals(workmateWithNoRestaurantChosen.getPickedRestaurant(), null);
        assertEquals(workmateWithNoRestaurantChosen.getIdPickedRestaurant(), null);
        assertEquals(workmateWithNoRestaurantChosen.getAddressRestaurant(), null);
        assertThat(workmateWithNoRestaurantChosen.getJoining(), is(false));
    }

    @Test
    public void workmate_withRestaurantChosen(){
        Workmate workmateWithRestaurantChosen = new Workmate(uid, picture, name, email, pickedRestaurant,
                idPickedRestaurant, addressRestaurant, joining);
        assertThat(workmateWithRestaurantChosen.getPickedRestaurant(), is(pickedRestaurant));
        assertThat(workmateWithRestaurantChosen.getIdPickedRestaurant(), is(idPickedRestaurant));
        assertThat(workmateWithRestaurantChosen.getAddressRestaurant(), is(addressRestaurant));
        assertTrue(String.valueOf(workmateWithRestaurantChosen.getJoining()), joining);
    }

    @Test
    public void notLikedRestaurant(){
        LikedRestaurant notLikedRestaurant = new LikedRestaurant(uid, null, false);
        assertEquals(notLikedRestaurant.getIdPickedRestaurant(), null);
        assertThat(notLikedRestaurant.isFavorite(), is(false));
    }

    @Test
    public void likedRestaurant(){
        LikedRestaurant likedRestaurant = new LikedRestaurant(uid, idPickedRestaurant, favorite);
        assertThat(likedRestaurant.getIdPickedRestaurant(), is(idPickedRestaurant));
        assertTrue(String.valueOf(likedRestaurant.isFavorite()), favorite);
    }

    @Test
    public void restaurantWithNoJoiningWorkmates(){
        Restaurant restaurantWithNoJoiningWorkmate = new Restaurant(idPickedRestaurant, pickedRestaurant, distanceFromUser, addressRestaurant,
                phoneNumber, website, restaurantPicture, latLng, 0, openingHour, rating);
        assertThat(restaurantWithNoJoiningWorkmate.getJoiningNumber(), is(0));
    }

    @Test
    public void restaurantWithJoiningWorkmates(){
        Restaurant restaurantWithJoiningWorkmate = new Restaurant(idPickedRestaurant, pickedRestaurant, distanceFromUser, addressRestaurant,
                phoneNumber, website, restaurantPicture, latLng, joiningNumber, openingHour, rating);
        restaurantWithJoiningWorkmate.setJoiningNumber(9);
        assertThat(restaurantWithJoiningWorkmate.getJoiningNumber(), is(joiningNumber));
    }
}