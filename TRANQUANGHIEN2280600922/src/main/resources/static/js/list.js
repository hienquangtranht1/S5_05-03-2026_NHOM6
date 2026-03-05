$(document).ready(function () {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books', 
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            let trHTML = '';
            $.each(data, function (i, item) {
                trHTML += '<tr id="book-' + item.id + '">' +
                    '<td class="text-center">' + item.id + '</td>' +
                    '<td>' + item.title + '</td>' +
                    '<td>' + item.author + '</td>' +
                    '<td class="text-end">' + item.price + ' VNĐ</td>' +
                    '<td class="text-center">' + (item.category ? item.category : 'N/A') + '</td>' +
                    '<td class="text-center">';

                if (isAdmin) {
                    trHTML += '<a href="/books/edit/' + item.id + '" class="btn btn-warning btn-sm me-1">Edit</a>';
                    trHTML += '<button class="btn btn-danger btn-sm" onclick="apiDeleteBook(' + item.id + ')">Delete</button>';
                }

                if (isUser) {
                    trHTML += '<form action="/books/add-to-cart" method="post" class="d-inline ms-1">' +
                        '<input type="hidden" name="id" value="' + item.id + '">' +
                        '<input type="hidden" name="name" value="' + item.title + '">' +
                        '<input type="hidden" name="price" value="' + item.price + '">' +
                        '<button type="submit" class="btn btn-success btn-sm" onclick="return confirm(\'Add to cart?\')">Add Cart</button>' +
                        '</form>';
                }

                trHTML += '</td></tr>';
            });
            
            $('#book-table-body').append(trHTML);
        },
        error: function (xhr, status, error) {
            console.error('Lỗi khi gọi API:', error);
        }
    });
});

function apiDeleteBook(id) {
    if (confirm('Are you sure you want to delete this book (via API)?')) {
        $.ajax({
            url: 'http://localhost:8080/api/v1/books/' + id,
            type: 'DELETE',
            success: function () {
                alert('Book deleted successfully!');
                $('#book-' + id).remove(); 
            },
            error: function (xhr) {
                alert('Error deleting book!');
            }
        });
    }
}