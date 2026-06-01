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
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
        Cart cart  = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new ApiException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.add(newCartItem);
        cart.setCartItems(cartItems);
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);



        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        if(authUtil.isAdmin()){
            List<Cart> carts = cartRepository.findAll();
            if (carts.isEmpty()) {
                throw new ApiException("No carts founds");
            }
            List<CartDTO> cartDTOS = carts.stream().map(cart ->
            {CartDTO map = modelMapper.map(cart, CartDTO.class);
                List<ProductDTO> products = cart.getCartItems().stream()
                        .map(p->{ProductDTO productDTO =modelMapper.map(p.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(p.getQuantity());
                        return productDTO;}).toList();
                map.setProducts(products);
                return map;
            }).toList();
            return cartDTOS;
        }else{
            throw new ApiException("You don't have privilege roles to perform this action");

        }


    }

    @Override
    public CartDTO getUserCart() {
        String email= authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart==null){
            throw new ApiException("no cart found for the user");
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setProducts(cart.getCartItems().stream().map(cartItem ->
        {
            ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
            productDTO.setQuantity(cartItem.getQuantity());
            return productDTO;
        }).toList());
        return  cartDTO;
    }

    @Override
    public CartDTO updateQuantityInCart(Long productId, Integer quantity) {
        Cart cart  = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }
        if (product.getQuantity() < quantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }


        cartItem.setQuantity(cartItem.getProduct().getQuantity()+quantity);

        cartItemRepository.save(cartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.stream().filter(e->e.getCartItemId()==cartItem.getCartItemId())
                        .findFirst()
                                .ifPresent(e->e.setQuantity(quantity));

        cart.setCartItems(cartItems);
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);



        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId",cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if(cartItem==null)
            throw new ApiException("Product "+product.getProductName()+" not available in cart");

        if (product.getQuantity() == 0) {
            throw new ApiException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < cartItem.getQuantity()+quantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }
        int newQuantity = cartItem.getQuantity()+quantity;
        if(newQuantity<0)
            throw new ApiException("Product can not be added in cart with negative quantity");
        if(newQuantity == 0)
            deleteProductFromCart(cartId,productId);
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItem updated = cartItemRepository.save(cartItem);
        if(updated.getQuantity()==0){
            cartItemRepository.deleteById(updated.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        cartDTO.setProducts(cartItems.stream().map(item ->{
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        }).toList());
        return cartDTO;

    }
@Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem == null)
            throw new ResourceNotFoundException("Product", "productId", productId);

        cart.setTotalPrice(cart.getTotalPrice()-
                (cartItem.getQuantity()*cartItem.getProductPrice()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product "+cartItem.getProduct().getProductName()+" deleted from your cart successfully";
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId",cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem == null)
            throw new ResourceNotFoundException("Product", "productId", productId);

        double cartPrice = cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice+(cartItem.getQuantity()*cartItem.getProductPrice()));
        cartItemRepository.save(cartItem);
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
