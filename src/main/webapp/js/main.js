$('#create-user-btn').click(function() {
    var username=$('#username').val();
    var buChoice=$('#select-bu').val();
    var putURL="/api/put/putAll/" + buChoice + "/" + username;
    
    if ( username == "" ) {
        $('.response-body').html('This username was not found. Please enter a valid shortname.');
    } else if ( buChoice == "" || buChoice == null ) {
        $('.response-body').html('Please select a valid Business Unit.');
    } else {
        $('.response-body').html('Please wait ...');
        $.ajax({
            type: 'PUT',
            url: putURL,
            success: function(resultt) {
                $('.response-body').html('Your user was created!<br>');
                $('.response-body').append('<div class="user-info">'+
                                                '<div class="left"></div>'+
                                                '<div class="center"></div>'+
                                                '<div class="right"></div>'+
                                           '</div>');
                                   
                $('.user-info .left').append('<div>API endpoint</div>');
                $('.user-info .left').append('<div>Space</div>');
                $('.user-info .left').append('<div>Password</div>');

                $('.user-info .center').append('<div>:</div>');
                $('.user-info .center').append('<div>:</div>');
                $('.user-info .center').append('<div>:</div>');

                $('.user-info .right').append('<div>'+resultt.api_endpoint+'</div>');
                $('.user-info .right').append('<div>'+resultt.space+'</div>');
                $('.user-info .right').append('<div>Your FEAD credentials</div>');
                $('.response-body').append('<div>Thanks for using my portal! Please consider a living proof for me (Micahel Hug) or just a high five if you are in Alphretta! </div>');
            },
            error: function(xhr, status, error) {
                console.log(error);
            },
            dataType: 'json'
        });
    }

});
