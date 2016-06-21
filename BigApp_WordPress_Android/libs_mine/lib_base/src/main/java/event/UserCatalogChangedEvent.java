package event;

import java.util.ArrayList;

import model.NavCatalog;

/**
 * Created by wjwu on 2015/9/2.
 */
public class UserCatalogChangedEvent {

    public ArrayList<NavCatalog> catalogList;

    public UserCatalogChangedEvent(ArrayList<NavCatalog> catalogList) {
        this.catalogList = catalogList;
    }

}
