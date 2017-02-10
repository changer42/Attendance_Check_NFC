/**
 * RegistrationController
 *
 * @description :: Server-side logic for managing registrations
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {

 	create: function (req, res) {

 		if(req.method=="POST"&&req.param("Registration",null)!=null)
 		{

 			Registration.create(req.param("Registration")).exec(function(err,model){


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

 			res.render( 'Registration/create');
 		}


 	},

 	index: function (req, res) {

 		Registration.find().exec(function(err, regs) {

 			res.render( 'Registration/index',{'registration':regs});
 			return;

 		});    
 	} ,

 	json: function (req, res)  {

 		Registration.find().exec(function(err, regs) {

 			res.json(regs);
 			return;

 		});    
 	},

 	view: function (req, res) {

 		//var uid=req.param("id",null);

    var id=req.param("id",null);

 		//Registration.findOne().where({uid:uid}).exec(function(err,model){

    Registration.findOne(id).exec(function(err,model){
      
      //console.log(model);

 			res.render('Registration/view',{'model':model});     

 		});
 	},
 	update: function (req, res) {

 		var id=req.param("id",null);

 		Registration.findOne(id).exec(function(err, model) {

 			if(req.method=="POST"&&req.param("Registration",null)!=null)
 			{

 				var p=req.param("Registration",null);


 				model.uid=p.uid;
 				model.cid=p.cid;
 				model.attendance=p.attendance;

 				model.save(function(err){

 					if (err) {

 						res.send("Error");

 					}else {

 						res.redirect( 'Registration/view/'+model.id);

 					}


 				});

 			}
 			else
 			{
        //console.log(model);

 				res.render( 'Registration/update',{'model':model});
 			}


 		});


 	},
 	
 	delete: function (req, res) {
 		
 		var id=req.param("id",null);
 		
 		Registration.findOne(id).exec(function(err, user) {

 			user.destroy(function(err) {
 				
 				res.redirect( 'Registration/index/');
 				
              // record has been removed
          });

 		});
 		
 	}



 };