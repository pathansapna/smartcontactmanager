console.log("This is script file");

const toggleSidebar=()=>{

    if($('.sidebar').is(":visible")) {
//    true
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%" )
    }else{
//    false
  $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%" )
    }

};

const search=()=>{
//    console.log("Searching...");
    let query = $("#search-input").val();
    console.log(query);

    if(query==''){
        $(".search-result").hide();

    }else{
        //search
        console.log(query);

        //Sending request to server
        let url = 'http://localhost:8282/search/${query}';

        fetch(url).then((response) => {
            return response.json();

        }).then((data) => {
            //data........
//            console.log(data);

            let text = `<div class='list-group'>`;

            data.forEach((contact) => {
                text +=`<a href ='/user/${contact.cid}/contact' class= 'list-group-item list-group-item-action'>${contact.name}</a>`
            });

            text +=`</div>`;
            $(".search-result").html(text);
                     $(".search-result").show();

        });


    }

};

//First request to server to create order

const paymentStart=() => {
    console.log("Payment started...");
    let amount = $("#payment_field").val()
    console.log(amount);
    if(amount=="" || amount==null)
    {
        alert("Amount is required !!");
        return;
    }
    //code..
    //WE are using ajax to send request to server to create order - jquery

    $.ajax({

        url: '/user/create_order',
        data: JSON.stringify({amount:amount, info: 'order_request'}),
        contentType: 'application/json',
        type: 'post',
        dataType: 'json',
        success: function(response){
            //Invoked when success
            console.log(response);
            if(response.status == "created"){
            //Open payment form
            let options = {
                key: '<KEY>',
                amount: response.amount,
                currency: 'INR',
                name: 'Smart Contact Manager',
                description: 'Donation',
                image: "https://i.ndtvimg.com/i/2017-06/wonder-woman-review_640x480_71496387846.jpg",
                order_id: response.id,
                handler: function(response){
                    console.log(response.razorpay_payment_id),
                    console.log(response.razorpay_order_id),
                    console.log(response.razorpay_signature),
                    console.log('Payment successful !!'),
//                    alert("Congrats !! Payment successful !!"),

                      updatePaymentOnServer(
                        response.razorpay_payment_id,
                        response.razorpay_order_id,
                         "paid");

                    swal("Good job!", "Your payment is successful!", "success");

                },
                "prefill": {
                        "name": "",
                        "email": "",
                        "contact": ""
                    },
                    "notes": {
                        "address": "Shrirampur"
                    },
                    "theme": {
                        "color": "#3399cc"
                    }

            };
            let rzp = new Razorpay(options);
            rzp.open();
            rzp.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
//                    alert("Oops, payment fail")
                    swal("Failed", "amount is required", "error");

            });


            }
        },
        error: function(error){
            //INvoked when error
            console.log(error);
            alert("Something went wrong !!");
        }

    })
};


function updatePaymentOnServer(payment_id,order_id,status){
        $.ajax({
        url: '/user/update_order',
                data: JSON.stringify({payment_id:payment_id, order_id: order_id, status:status}),
                contentType: 'application/json',
                type: 'post',
                dataType: 'json',
                success: function(response){
                    swal("Good job!", "Your payment is successful!", "success");


                },
                error:function(error){
                    swal("Failed", "Your payment is successful , but we did not get on server, we will contact you as soon as possible", "error");
},


                });
}