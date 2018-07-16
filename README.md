# MyPantry README
> project for CS 180: Mobile application development with Android

## Description
> A life organizing app designed to help reduce food waste.

## Manual

### MAIN VIEW
- Upon opening for the first time, the view will be mostly blank. This will change when the user inserts items using the add items button detailed later in the manual.
- The FAB with the “+” on it takes the user to the ADD ITEMS view
- The FAB with the wrench on it creates a popup that allows the user to sort items in the list according to specific item attributes.
“GENERATE RECIPES” will take the user to a separate page where they can view the top 10 recipes corresponding to ingredients that they select. 
- The [...] on the action bar creates a button that takes the user to the Metrics page.
- If there are items added to the list, they will appear on this view. 
- Each item will have a field indicating the name, the quantity of this item left, the group (tag) that the user designated, and the time until expiration. If the item is already expired, the view will let the user know how many days ago it expired and the item will turn a shade of red to further highlight that the item has expired. 
- Each item has a checkbox. When the checkbox is marked, GENERATE RECIPES will attempt to find recipes containing the items selected. 
- Upon selecting the item by touching it on the list, a dialog will pop up with the item’s name and three options: Edit, Delete, and 
Consume/Waste
- Edit will take the user to a page almost identical to ADD ITEM. The only differences are that the fields contain the existing information (which can be edited) and when the user presses save, it will update the item in the list, not create a new one.
- Delete will remove the item from the list, without generating any data for Metrics.
- Consume/waste will create another dialog. It will show “ITEM_NAME - Quantity Wasted” and on the next line there is a text view followed by a “/” and the item’s current quantity. The user can input any number up to the number shown. This number denotes the amount of food that was wasted out of the original quantity purchased. For example, if the user bought 12 eggs and used all of them, they would input ‘0’ to indicate that none of them were wasted. If the user bought 1 milk carton and drank half of it, then they would enter ‘0.5’. - Pressing “DONE” will confirm the selection and the item will be removed from the list. The information of the item will be shown in the Metrics page detailed later. 

### ADD ITEM FRAGMENT
- There are 5 different text views corresponding to item attributes. Each one can be inputted by selecting the text view and using the appropriate prompt that will be provided. Ex. When inserting the expiration date, a dialog will pop up with a calendar, allowing the user to select a specific date
- There are two buttons at the bottom of the view: SAVE and EXIT
- SAVE will take the user’s inputted data and it will show up on the main view when this fragment is removed. It will clear all text view fields, allowing the user to input another item. If the input is not correct, Then a Toast will show up informing the user that the input is not correct.
- EXIT will take the user back to the main page, discarding any unsaved item.

### METRICS FRAGMENT
- Upon viewing this page for the first time in the current month, the view will be mostly empty and will contain empty values indicating that there is no current data. This will be reset every month.
- The first number indicates the amount of money wasted in the current month.
- Underneath the number, there is a pie chart showing the user the amount of food wasted per given tag.
- Underneath the pie chart, there is a line chart showing the amount of money spent and wasted for the current year. Red line denotes wasted and Blue line denotes total spent or the month.
- Underneath the line graph there is additional information: most wasted item and the amount wasted, the item with the highest total cost and the amount spent.


### GENERATE RECIPES FRAGMENT
- If the user has selected items from the main view, then this page will show up when “GENERATE RECIPES” is pressed. 
- A list of recipes will appear along with the number of calories. 
- If a common item, such as “eggs” is selected, the user may have to wait for a moment for the results to show.
- Upon pressing the recipe, the user will be taken to a separate webpage in their browser with the recipe.

### SORT ITEMS
- Upon pressing the FAB with the wrench, a dialog will appear with 5 options.
- Expiration will sort based on the items closest to expiring.
- Tag and Name will sort the items alphabetically by either tag or name, respectively
- Quantity and price will sort in descending order of price or quantity, respectively.



