Basic Foodatory Documentation

Description: 
	Simple Android application designed to track what items you've purchased 
	and keep track of how long till they spoil.
	
Features:
	- Products: Add as many products as you want, divided into three main
	categories (Fresh Food, Dry Food, Condiments). Fresh Food contains
	expiry date and quantity, Dry Food contains quantity, and Condiments
	contains neither
	- Pantry: Place where Products that you own go. Stores when you added
	them, and the quantity (if applicable), so you know when they go bad.
	Can set up notifications to alert you when food item goes bad (default
	is daily)
	- Shopping List: Can add Products to the shopping list, which then
	becomes check list. Once you check something off and switch out of
	the shopping list, automatically added to your pantry, along with date
	purchased.
	- Recipes: Stores recipes, with what products they contain, an optional
	picture, and drag and drop directions
	
Design:
	There are 6 main ORM objects, which are as follows:
	- Product: Stores product information, such as type, etc.
	- Pantry: Stores a product id, along with date added
	- Shopping List: Stores a product id, along with quantity wanted
	- Recipe: Stores a list of products and Directions
	- Direction: Stores a recipe id, order of direction, and what direction is
	- RecipeProduct: Relates a product and recipe, with quantity detail
	
	