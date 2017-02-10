/**
 * StudentDController
 *
 * @description :: Server-side logic for managing Userinfoes
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {

 	create: function (req, res) {

 		if(req.method=="POST"&&req.param("StudentD",null)!=null)
 		{

 			StudentD.create(req.param("StudentD")).exec(function(err,model){


                  // Error handling
                  if (err) {

                  	res.send("Error:Sorry!Something went Wrong");

                  }else {
                  	res.send("Successfully Created!");
                    //res.redirect( ‘person/view/’+model.id);

                  }


                });

 		}
 		else
 		{

 			res.render( 'StudentD/create');
 		}


 	},



 	index: function (req, res) {

 		StudentD.find().exec(function(err, Students) {

 			res.render( 'StudentD/index',{'student':Students});
 			return;

 		});    
 	} ,

 	json: function (req, res)  {

 		StudentD.find().exec(function(err, Students) {

 			res.json(Students);
 			return;

 		});    
 	},
 	view: function (req, res) {

 		var id=req.param("id",null);

 		StudentD.findOne(id).exec(function(err,model){

 			res.render( 'StudentD/view',{'model':model});     

 		});    


 	},
 	update: function (req, res) {

 		var id=req.param("id",null);

 		StudentD.findOne(id).exec(function(err, model) {

 			if(req.method=="POST"&&req.param("StudentD",null)!=null)
 			{

 				var p=req.param("StudentD",null);


 				model.name=p.name;
 				model.sid=p.sid;
       			model.uid=p.uid;

        		model.save(function(err){

          		if (err) {

           			res.send("Error");

         		}else {

          		 	res.redirect( 'StudentD/view/'+model.id);

         		}


       		});

      	}
      	else
      	{

       res.render( 'StudentD/update',{'model':model});
     }


   });


 	},
 	delete: function (req, res) {
 		
 		var id=req.param("id",null);
 		
 		StudentD.findOne(id).exec(function(err, user) {

 			user.destroy(function(err) {
 				
 				res.redirect( 'StudentD/index/');
 				
              // record has been removed
            });

 		});
 		
 	}



};

