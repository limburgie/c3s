package be.webfactor.c3s.shopping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

    @Autowired private ShoppingCartSerializer shoppingCartSerializer;

    public void initializeShoppingCart(String shoppingCartEncoded) {
        ShoppingCartThreadLocal.setShoppingCart(shoppingCartSerializer.deserialize(shoppingCartEncoded));
    }
}
