/*
 * @author: Jason Howard (Jason.Howard@Fiserv.com)
 * @author: Michael Hug (Michael.Hug@Fiserv.com)
 */

$(document).ready(function() {
    var buURL="api/get/orgs/";
    $.ajax({
        type: 'GET',
        url: buURL,
        success: function(result) {
            var buArray = {};
            for(var i in result) {
                buArray[i] = result[i];
            }
            //sort this later when you get five seperate complaints
            $('#select-bu').append("<option value='' selected='selected' disabled>Business Unit</option>");
            for(var key in buArray) {
                if (!key.toLowerCase().includes("spring") &&
                        !key.toLowerCase().includes("broker") &&
                        !key.toLowerCase().includes("splunk") &&
                        !key.toLowerCase().includes("system")) {
                                $('#select-bu').append("<option value='" + buArray[key] + "'>" + key + "</option>");
                }
            }
        },
        error: function(xhr, status, error) {
            console.log(error);
        },
        dataType: 'json'
    });  
});