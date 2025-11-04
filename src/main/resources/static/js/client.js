$(document).ready(function () {
       $('.delete-client').click(function () {
           const cin = $(this).data('cin');
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
				console.log(cin);
                   $.ajax({
                       url: '/clients/delete',
                       type: 'POST',
                       data: { cin: cin },
                       success: function () {
                           $('#' + cin).remove();
                           Swal.fire('Deleted!', 'Client has been deleted.', 'success');
                       },
                       error: function () {
                           Swal.fire('Error!', 'Server error occurred.', 'error');
                       }
                   });
               }
           });
       });
   });