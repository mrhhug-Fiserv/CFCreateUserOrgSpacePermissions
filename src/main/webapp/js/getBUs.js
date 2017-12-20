/*
 * @author: Jason Howard (Jason.Howard@Fiserv.com)
 */

$(document).ready(function() {
    var buURL="api/get/BU";
    $.ajax({
        type: 'GET',
        url: buURL,
        success: function(result) {
            var buArray = [];
            for(var i in result) {
                buArray.push([i, result[i]]);
            }
            var sortedBUs = buArray.sort();

            $('#select-bu').append("<option value='' selected='selected' disabled>Business Unit</option>");
            $.each(sortedBUs, function(k, v) {
		$('#select-bu').append("<option value='" + v[1] + "'>" + v[0] + "</option>");
            });
        },
        error: function(xhr, status, error) {
            console.log(error);
        },
        dataType: 'json'
    });  
});
