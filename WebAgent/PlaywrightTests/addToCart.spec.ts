import { test, expect } from '@playwright/test';

test('Add product to cart', async ({ page }) => {
  await page.goto('https://sauce-demo.myshopify.com/');
  await page.getByPlaceholder('Search').fill('grey jacket');
  await page.getByRole('button', { name: 'Submit' }).click();
  await page.waitForLoadState('networkidle');
  await page.getByRole('link', { name: 'Grey jacket' }).click();
  await page.getByRole('button', { name: 'Add to cart' }).click();
const cartLink = page.getByRole('link', { name: /My Cart/ });
await expect(cartLink).toContainText('(1)', { timeout: 15000 });
});
