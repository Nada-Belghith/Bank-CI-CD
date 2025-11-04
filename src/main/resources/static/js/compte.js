
$('.delete-compte').click(function() {
	const rib = $(this).data('rib');
	Swal.fire({
		title: 'Are you sure?',
		text: "You won't be able to revert this!",
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'Yes, delete it!'
	}).then((result) => {
		if (result.isConfirmed) {
			$.ajax({
				url: '/comptes/delete',
				type: 'POST',
				data: { rib: rib },
				success: function() {
					$('#' + rib).remove();
					Swal.fire('Deleted!', 'Compte has been deleted.', 'success');
				},
				error: function() {
					Swal.fire('Error!', 'Server error occurred.', 'error');
				}
			});
		}
	});
});

$(document).ready(function() {
	$("form").submit(function(event) {
		var solde = parseFloat($("#solde").val());
		if (solde < 0) {
			Swal.fire('Error!', 'Solde cannot be negative.', 'error');
			event.preventDefault();
		}
		var clientCin = $("#clientCinHidden").val();
		if (!clientCin) {
			Swal.fire('Error!', 'Please select a valid client from the autocomplete list.', 'error');
			event.preventDefault();
		}
	});
});


$('#clientCin').autocomplete({
	source: function(request, response) {
		$.ajax({
			url: '/clients/all-json',
			dataType: 'json',
			data: {
				term: request.term,
				field: 'cin'
			},
			success: function(data) {
				var filteredData = data.filter(function(item) {
					return item.cin.toLowerCase().indexOf(request.term.toLowerCase()) > -1;
				});

				response($.map(filteredData, function(item) {
					return {
						label: item.cin + ' - ' + item.nom + ' ' + item.prenom,
						value: item.cin
					};
				}));
			},
			error: function() {
				Swal.fire('Error!', 'Unable to fetch clients for autocomplete.', 'error');
			}
		});
	},
	minLength: 2,
	select: function(event, ui) {
		$('#clientCin').val(ui.item.value);
		$('#clientCinHidden').val(ui.item.value);
		return false;
	}
});
