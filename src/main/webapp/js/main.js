$('#create-user-btn').click(function() {
    var username=$('#username').val();
    var buChoice=$('#select-bu').val();
    //var getURL="api/userSpacePermissionCheck/" + username;
    var getURL="api/is/LDAPUserPresent/" + username;
    var putURL="api/createUserSpacePermissions/" + buChoice + "/" + username;
    
    console.log("Calling: " + putURL);
    
    if ( username == "" ) {
        $('.response-body').html('This username was not found. Please enter a valid shortname.');
    } else if ( buChoice == "" || buChoice == null ) {
        $('.response-body').html('Please select a valid Business Unit.');
    } else {
        $('.response-body').html('Please wait ...');
        $.ajax({
            type: 'GET',
            url: getURL,
            success: function(result){
                if(!result.isLDAPUserPresent) {
                    $('.response-body').html('This username was not found. Please enter a valid shortname.');
                } else {
                    console.log("LDAP User Found: " + username);
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
                            $('.user-info .left').append('<div>Org</div>');
                            $('.user-info .left').append('<div>Space</div>');
                            $('.user-info .left').append('<div>Username</div>');

                            $('.user-info .center').append('<div>:</div>');
                            $('.user-info .center').append('<div>:</div>');
                            $('.user-info .center').append('<div>:</div>');
                            $('.user-info .center').append('<div>:</div>');

                            $('.user-info .right').append('<div>'+resultt.api_endpoint+'</div>');
                            $('.user-info .right').append('<div>'+resultt.org+'</div>');
                            $('.user-info .right').append('<div>'+resultt.space+'</div>');
                            $('.user-info .right').append('<div>'+resultt.username+'</div>');
                        },
                        error: function(xhr, status, error) {
                            console.log(error);
                        },
                        dataType: 'json'
                    });
                }
            },
            error: function(xhr, status, error) {
                console.log(error);
            },
            dataType: 'json'
        });
    }

});
