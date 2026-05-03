# Test Cases for Jira Story SHOP-2: Cart and Checkout Management

## Test Case 1: User must be logged in to add items to cart
- **Precondition**: User is not authenticated.
- **Steps**:
  1. Navigate to the product listing page.
  2. Click “Add to Cart” for a product.
- **Expected Result**: System redirects to the login page; cart contents remain unchanged.

## Test Case 2: Guest user cannot add items to cart
- **Precondition**: User is not logged in (guest session).
- **Steps**:
  1. Browse to a product detail page.
  2. Attempt to add the product to the cart.
- **Expected Result**: “Add to Cart” button is disabled or a prompt appears requiring login.

## Test Case 3: Successful addition of an item to the cart
- **Precondition**: User is logged in.
- **Steps**:
  1. Select a product.
  2. Click “Add to Cart”.
- **Expected Result**: Item appears in the cart sidebar with correct quantity (1) and price.

## Test Case 4: Remove item from cart
- **Precondition**: Item is present in the cart.
- **Steps**:
  1. Click the “Remove” link for the item.
- **Expected Result**: Item disappears from cart; cart total updates accordingly.

## Test Case 5: Update cart item quantity
- **Precondition**: Item is present in the cart.
- **Steps**:
  1. Click the quantity selector or “+” button to increase quantity.
  2. Click “Update” or confirm change.
- **Expected Result**: Cart reflects the new quantity; total price updates proportionally.

## Test Case 6: Cart expires after 7 days of inactivity
- **Precondition**: Item(s) are in the cart; user is logged in.
- **Steps**:
  1. Do not interact with the cart for 8 days.
- **Expected Result**: Cart is automatically cleared; user sees an empty cart or a message indicating expiration.

## Test Case 6: Checkout with valid payment method succeeds
- **Precondition**: Valid payment method is saved in user profile; user is logged in; items are in cart.
- **Steps**:
  1. Proceed to checkout.
  2. Review order summary.
  3. Submit payment.
- **Expected Result**: Transaction is processed, order is created, and a confirmation page is displayed.

## Test Case 7: Checkout fails with invalid payment method
- **Precondition**: User attempts checkout with a payment method that is declined or expired.
- **Steps**:
  1. Initiate checkout.
  2. Enter or select an invalid payment method.
  3. Submit payment.
- **Expected Result**: Error message is shown indicating payment failure; cart remains unchanged.

## Test Case 7: Secure checkout uses HTTPS
- **Precondition**: User is on the checkout page.
- **Steps**:
  1. Verify the URL begins with https://.
- **Expected Result**: Connection is encrypted; browser shows a secure lock icon.

## Test Case 8: Order confirmation email is sent after successful checkout
- **Precondition**: Successful payment; user’s email is configured.
- **Steps**:
  1. Complete checkout.
- **Expected Result**: Confirmation email with order details is received by the user.
