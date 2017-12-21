const colors = Object.freeze(['BlueViolet', 'Brown', 'BurlyWood', 'CadetBlue', 'Chocolate', 'Coral', 'CornflowerBlue', 'Crimson', 'DarkCyan', 'DarkGoldenRod', 'DarkMagenta',
  'DarkOliveGreen', 'DarkOrange', 'DarkOrchid', 'DarkRed', 'DarkSalmon', 'DarkSlateBlue', 'DeepPink']);

function getStyle() {
  function nodeStyles(stylesheet) {
    stylesheet.selector('node')
        .css({
          'shape':              'data(faveShape)',
          'width':              'mapData(weight, 40, 80, 20, 60)',
          'content':            'data(content)',
          'text-valign':        'center',
          'text-outline-width': 2,
          'text-outline-color': 'data(color)',
          'background-color':   'data(color)',
          'color':              '#fff'
        })
  }

  function edgeStyles(stylesheet) {
    stylesheet.selector('edge')
        .css({
          'curve-style':        'bezier',
          'opacity':            0.9,
          'label':              '',
          'width':              'mapData(strength, 70, 100, 2, 6)',
          'target-arrow-shape': 'triangle',
          //  'source-arrow-shape': 'circle',
          'line-color':         'data(color)',
          'color':              'white',
          'text-rotation':      'autorotate',
          'source-arrow-color': 'data(color)',
          'target-arrow-color': 'data(color)'
        })
    ;

    stylesheet.selector("edge.discolored").css({
      'line-color':         'gray',
      'source-arrow-color': 'gray',
      'target-arrow-color': 'gray'
    });
    stylesheet.selector("edge.selected").css({'label': 'data(content)'})
  }

  var stylesheet = cytoscape.stylesheet();

  nodeStyles(stylesheet);
  edgeStyles(stylesheet);
  return stylesheet
      .selector(':selected')
      .css({
        'border-width': 3,
        'border-color': '#333'
      })
}

// Startup
$(function () {


  /**
   * Convert graph returned from server to Cytoscape format
   * @param graph
   * @returns {{nodes: (Array|*), edges: (Array|*)}}
   */
  function toCyto(graph) {

    var nodes = graph.nodes;

    var edges = graph.edges;

    if (nodes) {
      var displayedNodes = nodes.map(function (n, i) {
        // Mutate nodes to preserve styles for edges
        n['faveShape'] = 'ellipse';
        n['weight'] = 50;
        n['color'] = colors[i % colors.length];
        return {
          data: n
        }
      });
    }


    if (edges) {
      var displayedEdges = edges.map(function (e) {
        var sourceNode = nodes.find(function (n) {
          return n.id === e.source
        });
        e['strength'] = 50;
        e['color'] = sourceNode['color'];
        return {data: e}
      });
    }


    return {
      nodes: displayedNodes || [],
      edges: displayedEdges || []
    }
  }


  // Load graph
  fetch("/graph").then(function (response) {
    return response.json().then(function (j) {
      return j
    })
  }).then(function (graph) {

    function gridLayout() {
      var grid = Math.floor(Math.sqrt(graph.nodes.length));

      return {
        name: 'grid',
        rows: grid,
        cols: grid
      }
    }


    var cy = cytoscape({
      container: $('#content'),
      layout:    {
        name:           'concentric',
        // This decides how far away the node will be, based on how many outbound connections it has
        concentric:     function (node) {
          return node.degree() * 100;
        },
        levelWidth:     function (nodes) {
          return nodes.maxDegree() * 10;
        },
        minNodeSpacing: 100,
        fit:            false
      },

      style:    getStyle(),
      elements: toCyto(graph)
    });

    cy.on("click", "node", function (n) {

      var selectedNode = n.cyTarget;
      if (!selectedNode.hasClass('selected')) {
        selectedNode.addClass('selected');
        cy.edges().removeClass('selected').addClass('discolored');
        n.cyTarget.connectedEdges().removeClass('discolored').addClass('selected');
      }
      else {
        selectedNode.removeClass('selected');
        cy.edges().removeClass('selected').removeClass('discolored');
      }
    });
  });
});