var Log = {
  elem: false,

  write: function(text){
    if (!this.elem)
      this.elem = document.getElementById('log');
    this.elem.innerHTML = text;
    this.elem.style.left = ($jit.id('container').offsetWidth - this.elem.offsetWidth) / 2 + 'px';
  }
};

function displayDetails(node) {
  var avatarResource = node.data.thumbnail;
  console.log(avatarResource);
  if (!avatarResource) {
    avatarResource = '/social-portlet/skin/social/portlet/UIProfilePortlet/DefaultSkin/background/BLAvatar.gif';
  }
  var avatar = '<img id="avatar" alt="" src="'+ avatarResource +'" />';
  var info =   '<div class="Info">'
             +   '<h2 id="displayname" class="Line">' + node.name + '</h2>';
  if (node.data.email && node.data.email != 'null')
    info += '<div class="Email">' + node.data.email + '</div>';
  info += '</div>';
  
  var friends = '<div id="friends" class="friends ClearFix"><ul>';
  node.eachAdjacency(function(adj) {
    var child = adj.nodeTo;
    friends += "<li>" + child.name + " " + "<div class=\"relation\"></div></li>";
  });
  
  friends += "</ul></div>";
  
  var html = avatar + info + friends;
  $("#userinfo").html(html);
}

function init() {
  var currentView = gadgets.views.getCurrentView().getName();
  var distance = 125;
  if (currentView == 'canvas') {
    distance = 250;
  }
  var rgraph = new $jit.RGraph({
    levelDistance: distance,
    //Where to append the visualization
    injectInto: 'infovis',
    //Optional: create a background canvas that plots
    //concentric circles.
    background: {
      CanvasStyles: {
        strokeStyle: '#1A1A1A'
      }
    },
    //Add navigation capabilities:
    //zooming by scrolling and panning.
    Navigation: {
      enable: true,
      panning: true,
      zooming: 15
    },
    //Set Node and Edge styles.
    Node: {
      color: '#ddeeff'
    },
    
    Edge: {
      color: '#C17878',
      lineWidth:1.5
    },
    
    onBeforeCompute: function(node){
      Log.write("Centering " + node.name + "...");
    },
    
    onComplete: function () {
      Log.write('');
      if ($jit.id('inner-details')) {
        var node = rgraph.graph.getClosestNodeToOrigin("current");
        displayDetails(node);
      }
      gadgets.window.adjustHeight($("#container").height());
    },
    
    
    //Add the name of the node in the correponding label
    //and a click handler to move the graph.
    //This method is called once, on label creation.
    
    onCreateLabel: function(domElement, node){
      domElement.innerHTML = node.name;
      domElement.onclick = function(){
        reloadData(node.id);
        rgraph.onClick(node.id, {
          onComplete: function() {
          }
        });
        $jit.id('infovis').innerHTML = "";
        rgraph.refresh(true);
      };
    },
    //Change some label dom properties.
    //This method is called each time a label is plotted.
    onPlaceLabel: function(domElement, node){
      var style = domElement.style;
      style.display = '';
      style.cursor = 'pointer';
      
      if (node._depth <= 1) {
        style.fontSize = "0.8em";
        style.color = "#ccc";
        
      } else if(node._depth == 2){
        style.fontSize = "0.7em";
        style.color = "#494949";
        
      } else {
        style.display = 'none';
      }
      
      var left = parseInt(style.left);
      var w = domElement.offsetWidth;
      style.left = (left - w / 2) + 'px';
    }
  });
  //load JSON data
  rgraph.loadJSON(json);
  //trigger small animation
  rgraph.graph.eachNode(function(n) {
    var pos = n.getPos();
    pos.setc(-200, -200);
  });
  rgraph.compute('end');
  rgraph.fx.animate({
    modes:['polar'],
    duration: 2000
  });
  //end
  //append information about the root relations in the right column
  //$jit.id('inner-details').innerHTML = rgraph.graph.getNode(rgraph.root).data.relation;  
}
