/**
 * AttendanceDController
 *
 * @description :: Server-side logic for managing Attendances
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {

 	create: function (req, res) {

 		if(req.method=="POST"&&req.param("AttendanceD",null)!=null)
 		{

 			AttendanceD.create(req.param("AttendanceD")).exec(function(err,model){
       // Error handling
       if (err) {
          res.send("Error:Sorry!Something went Wrong");
       }else {
          res.send("Successfully Created!");
         //res.redirect( ‘person/view/’+model.id);
        }
    });

      //every time we add an attendace into this Attendace table,
      //we increte the attendance attribute by 1 in table registration where uid is 123457

      // Registration.findOne().where({uid:123457}).exec(function(err, model) {
      //   //var p=req.param("Registration",null);
      //   console.log(model);
      //   // model.uid=p.uid;
      //   // model.cid = p.cid;
      //   model.attendance ++;
      //   // model.attendance=p.attendance;
      //   model.save(function(err){
      //     console.log("here");
      //     /* if (err) {
      //          res.send("Error");
      //        } else {
      //          res.redirect( 'Registration/view/'+model.id);
      //        };
      //     */
      //   });
      // });
      // //finish this modification in registration table



    }
    else
    {

      res.render( 'AttendanceD/create');
    }


  },



  index: function (req, res) {

   AttendanceD.find().exec(function(err, Attendances) {

    res.render( 'AttendanceD/index',{'attendance':Attendances});
    return;

  });    
 } ,

 json: function (req, res)  {

   AttendanceD.find().exec(function(err, Attendances) {

    res.json(Attendances);
    return;

  });    
 },
 view: function (req, res) {

   var id=req.param("id",null);

   AttendanceD.findOne(id).exec(function(err,model){

    res.render( 'AttendanceD/view',{'model':model});     

  });    


 },
 update: function (req, res) {

   var id=req.param("id",null);

   AttendanceD.findOne(id).exec(function(err, model) {

    if(req.method=="POST"&&req.param("AttendanceD",null)!=null)
    {

     var p=req.param("AttendanceD",null);


     model.code=p.code;
     model.uid=p.uid;

     model.save(function(err){

      if (err) {

       res.send("Error");

     }else {

       res.redirect( 'AttendanceD/view/'+model.id);

     }


   });

   }
   else
   {

     res.render( 'AttendanceD/update',{'model':model});
   }


 });


 },
 delete: function (req, res) {

   var id=req.param("id",null);

   AttendanceD.findOne(id).exec(function(err, user) {

    user.destroy(function(err) {

     res.redirect( 'AttendanceD/index/');

              // record has been removed
            });

  });

 }



};

