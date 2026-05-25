package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl  implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),productId
        );
        if(cartItem != null){
            throw new ApiException("Product "+ product.getProductName()+ " already added to cart");
        }
        if(product.getQuantity()==0){
            throw new ApiException("Product "+ product.getProductName()+ " is out of stock");
        }
        if(product.getQuantity()<quantity){
            throw new ApiException("Please make a order of the " +product.getProductName()
            +" less than or equal to the quantity "+product.getQuantity());
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice() * quantity));
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productDTOS = cartItems.stream().map(item ->{
                ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
        map.setQuantity(item.getQuantity());
        return  map;}).toList();

        Cart newCart = cartRepository.save(cart);
        CartDTO cartDTO = modelMapper.map(newCart, CartDTO.class);
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }
    private  Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser((authUtil.loggedInUser()));
        return cartRepository.save(cart);
    }
}
