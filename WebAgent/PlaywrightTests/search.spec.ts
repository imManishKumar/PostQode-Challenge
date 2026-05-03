import { test, expect } from '@playwright/test';

test('Search for a product', async ({ page }) => {
  await page.goto('https://sauce-demo.myshopify.com/');
await page.getByPlaceholder('Search').fill('grey jacket');
await page.getByRole('button', { name: 'Submit' }).click();
  await page.waitForLoadState('networkidle');
  await expect(page).toHaveURL(/.*search/);
  await page.screenshot({ path: 'search_result.png' });
});
