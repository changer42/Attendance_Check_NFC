/**
 * UserInfoController
 *
 * @description :: Server-side logic for managing Userinfoes
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {

 	create: function (req, res) {

 		// if(req.method=="POST")
    if(req.method=="POST"&&req.param("UserInfo",null)!=null)
 		{
      console.log(req.param);
 			UserInfo.create(req.param("UserInfo")).exec(function(err,model){


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

 			res.render( 'UserInfo/create');
 		}


 	},



 	index: function (req, res) {

 		UserInfo.find().exec(function(err, Userinfoes) {

 			res.render( 'UserInfo/index',{'userInfo':Userinfoes});
 			return;

 		});    
 	} ,

 	json: function (req, res)  {

 		UserInfo.find().exec(function(err, Userinfoes) {

 			res.json(Userinfoes);
 			return;

 		});    
 	},
 	view: function (req, res) {

 		var id=req.param("id",null);

 		UserInfo.findOne(id).exec(function(err,model){

 			res.render( 'UserInfo/view',{'model':model});     

 		});


 	},
 	update: function (req, res) {

 		var id=req.param("id",null);

 		UserInfo.findOne(id).exec(function(err, model) {

 			if(req.method=="POST"&&req.param("UserInfo",null)!=null)
 			{

 				var p=req.param("UserInfo",null);


 				model.name=p.name;
 				model.age=p.age;

 				model.save(function(err){

 					if (err) {

 						res.send("Error");

 					}else {

 						res.redirect( 'UserInfo/view/'+model.id);

 					}


 				});

 			}
 			else
 			{

 				res.render( 'UserInfo/update',{'model':model});
 			}


 		});


 	},
 	delete: function (req, res) {
 		
 		var id=req.param("id",null);
 		
 		UserInfo.findOne(id).exec(function(err, user) {

 			user.destroy(function(err) {
 				
 				res.redirect( 'UserInfo/index/');
 				
              // record has been removed
          });

 		});
 		
 	}



 };

