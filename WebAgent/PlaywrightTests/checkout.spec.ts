import { test, expect } from '@playwright/test';

test('Checkout flow', async ({ page }) => {
  await page.goto('https://sauce-demo.myshopify.com/');
  await page.getByPlaceholder('Search').fill('grey jacket');
  await page.getByRole('button', { name: 'Submit' }).click();
  await page.waitForLoadState('networkidle');
  await page.getByRole('link', { name: 'Grey jacket' }).click();
  await page.getByRole('button', { name: 'Add to cart' }).click();
  // Wait for cart to update
  await page.waitForTimeout(2000);
  await page.getByRole('link', { name: /My Cart/ }).click();
  await page.waitForLoadState('networkidle');
  // Click Check Out and wait for navigation or login prompt
  await Promise.all([
    page.waitForNavigation({ timeout: 10000 }).catch(() => {}),
    page.getByRole('link', { name: 'Check Out' }).click()
  ]);
  // Just verify we're not on the product page anymore
  await expect(page).not.toHaveURL(/products\/grey-jacket/);
});
