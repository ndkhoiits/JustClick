function init(){
  if (json){
    var infovis = document.getElementById('infovis');
    
    // init Hypertree
    var ht = new $jit.Hypertree({
      // id of the visualization container
      injectInto: 'infovis',
      // canvas width and height
      // Change node and edge styles such as
      // color, width and dimensions.
      Node: {
        dim: 9,
        color: "#f00"
      },
      Edge: {
        lineWidth: 2,
        color: "#088"
      },
      onBeforeCompute: function(node) {
        //        ht.refresh();
        //        ht.controller.onComplete();
      },
      
      onComplete: function() {
        if ($jit.id('inner-details')) {
          var node = ht.graph.getClosestNodeToOrigin("current");
          var html = "<h4>" + node.data.displayName + "</h4>";
          $jit.id('inner-details').innerHTML = html;
        }
        gadgets.window.adjustHeight($("#container").height());
      },
      
      // Attach event handlers and add text to the
      // labels. This method is only triggered on label
      // creation
      onCreateLabel: function(label, node){
        label.innerHTML = node.id;
        label.onclick = function() {
          //reloadData(node.id);
          ht.onClick(node.id, {
                  onComplete: function() {
                    ht.controller.onComplete();
                  }
          });
          //ht.refresh(true);
          //console.log(json);
        }
      },
    });
        
    // load JSON data.
    ht.loadJSON(json);
    // compute positions and plot.
    ht.refresh();
    // end
    ht.controller.onComplete();
  }
}
                                