/**
 * StudentController
 *
 * @description :: Server-side logic for managing Userinfoes
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {

 	create: function (req, res) {

 		if(req.method=="POST"&&req.param("Student",null)!=null)
 		{

 			Student.create(req.param("Student")).exec(function(err,model){


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

 			res.render( 'Student/create');
 		}


 	},



 	index: function (req, res) {

 		Student.find().exec(function(err, Students) {

 			res.render( 'Student/index',{'student':Students});
 			return;

 		});    
 	} ,

 	json: function (req, res)  {

 		Student.find().exec(function(err, Students) {

 			res.json(Students);
 			return;

 		});    
 	},
 	view: function (req, res) {

 		var id=req.param("id",null);

 		Student.findOne(id).exec(function(err,model){

 			res.render( 'Student/view',{'model':model});     

 		});    


 	},
 	update: function (req, res) {

 		var id=req.param("id",null);

 		Student.findOne(id).exec(function(err, model) {

 			if(req.method=="POST"&&req.param("Student",null)!=null)
 			{

 				var p=req.param("Student",null);


 				model.name=p.name;
 				model.sid=p.sid;
        model.uid=p.uid;

        model.save(function(err){

          if (err) {

           res.send("Error");

         }else {

           res.redirect( 'Student/view/'+model.id);

         }


       });

      }
      else
      {

       res.render( 'Student/update',{'model':model});
     }


   });


 	},
 	delete: function (req, res) {
 		
 		var id=req.param("id",null);
 		
 		Student.findOne(id).exec(function(err, user) {

 			user.destroy(function(err) {
 				
 				res.redirect( 'Student/index/');
 				
              // record has been removed
            });

 		});
 		
 	}



};

