const itemList = document.getElementById('item-list');
const addBtn = document.getElementById('add-btn');
const saveBtn = document.getElementById('save-btn');
const statusMessage = document.getElementById('status-message');
const emptyMessage = document.getElementById('empty-message');

addBtn.onclick = function () {
    emptyMessage.style.display = 'none';

    const item = document.createElement('div');
    item.className = 'item';

    const itemNumber = document.createElement('div');
    itemNumber.className = 'item-number';

    const itemContent = document.createElement('div');
    itemContent.className = 'item-content';

    const foodField = document.createElement('div');
    foodField.className = 'item-field';

    const foodLabel = document.createElement('label');
    foodLabel.textContent = 'Food Name';

    const foodInput = document.createElement('input');
    foodInput.type = 'text';
    foodInput.placeholder = 'Enter food name...';
    foodInput.required = true;

    foodField.appendChild(foodLabel);
    foodField.appendChild(foodInput);

    const gramsField = document.createElement('div');
    gramsField.className = 'item-field';

    const gramsLabel = document.createElement('label');
    gramsLabel.textContent = 'Grams';

    const gramsInput = document.createElement('input');
    gramsInput.type = 'number';
    gramsInput.placeholder = 'Enter grams...';
    gramsInput.required = true;
    gramsInput.min = "1";

    gramsField.appendChild(gramsLabel);
    gramsField.appendChild(gramsInput);

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'Delete';
    deleteBtn.onclick = function () {
        item.remove();
        updateItemNumbers();

        if (itemList.querySelectorAll('.item').length === 0) {
            emptyMessage.style.display = 'block';
        }
    };

    itemContent.appendChild(foodField);
    itemContent.appendChild(gramsField);

    item.appendChild(itemNumber);
    item.appendChild(itemContent);
    item.appendChild(deleteBtn);

    itemList.appendChild(item);

    updateItemNumbers();
};

saveBtn.onclick = async function () {
    const items = itemList.querySelectorAll('.item');


    let isValid = true;
    items.forEach(item => {
        const foodInput = item.querySelector('input[type="text"]');
        const gramsInput = item.querySelector('input[type="number"]');
    });


    const params = new URLSearchParams();
    params.append('productCount', items.length);

    items.forEach(item => {
        const foodInput = item.querySelector('input[type="text"]');
        const gramsInput = item.querySelector('input[type="number"]');

        params.append('food', foodInput.value.trim());
        params.append('gram', gramsInput.value);
    });

    try {
        const url = `http://localhost:8080/products/CalculateCalories?${params.toString()}`;
        console.log(url)
        let response = await axios.get(url)
        console.log(response)
        let summary = ""
        for (let i = 0; i < response.data.length; i++) {
            summary += `<p>${response.data[i]}</p>`
        }
        showTotal(summary, true)
    } catch (error) {
        showTotal("PIZDA \n" + error.message, false)
        showTotal(`<p>PIZDA</p><p>${error.message}</p>`, false)
    }
};

function updateItemNumbers() {
    const items = itemList.querySelectorAll('.item');

    items.forEach((item, index) => {
        const itemNumber = item.querySelector('.item-number');
        itemNumber.textContent = (index + 1) + '.';
    });
}


function showTotal(message, isSuccess) {
    statusMessage.innerHTML = message;
    statusMessage.className = 'status ' + (isSuccess ? 'success' : 'error');
    statusMessage.style.display = 'block';

    setTimeout(() => {
        statusMessage.style.display = 'none';
    }, 15000);
}

statusMessage.onclick = function hideStatus() {
    statusMessage.style.display = 'none';
}
