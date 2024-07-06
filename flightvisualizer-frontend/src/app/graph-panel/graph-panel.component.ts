import {Component, ElementRef, ViewChild} from '@angular/core';
import {Data, Network, Options} from "vis-network";
import { DataSet } from "vis-data"
import { Node, Edge } from "../core/dto/airport"
import {Subject} from "rxjs";
import {DataStoreService} from "../core/service/data-store.service";

@Component({
  selector: 'app-graph-panel',
  templateUrl: './graph-panel.component.html',
  styleUrl: './graph-panel.component.css'
})
export class GraphPanelComponent {

  constructor(private dataStoreService: DataStoreService) {
  }

  @ViewChild('menuDiv', { static: true }) menuDiv!: ElementRef;

  @ViewChild('treeContainer', { static: true }) treeContainer!: ElementRef;

  menuStatus: boolean = true;

  selectNode: any;
  prevSelectNode: any;

  private data!: Data;

  private nodes!: DataSet<Node>;

  private edges!: DataSet<Edge>;

  private network!: Network;

  private nodeNo: number = 6;

  onButtonClick() {

    let nodes: Node[] = [];
    let i = 0;
    this.dataStoreService.getAllAirports().forEach(airport => {
      nodes.push({
        id: i,
        label: airport.iataCode
      })
      i++;
    });

    let nodeDataSet = new DataSet(nodes);

    let edges: Edge[] = [];
    i = 0;
    this.dataStoreService.getAllLegRenders().forEach(leg => {
      const originId = nodes.find(n => n.label == leg.originAirportIataCode)?.id;
      const destinationId = nodes.find(n => n.label == leg.destinationAirportIataCode)?.id;

      if(!originId || !destinationId) return;
      edges.push({
        id: i,
        from: originId,
        to: destinationId
      })
      i++;
    });

    let edgeDataSet = new DataSet(edges);

    this.nodes = nodeDataSet;
    this.edges = edgeDataSet;
    this.data = {
      nodes: this.nodes,
      edges: this.edges,
    };

    this.network = new Network(
        this.treeContainer.nativeElement,
        this.data,
        this.getNetworkOptions()
    );
    this.network.redraw()
    this.network.setSize("500", "1000")
  }

  getNetworkOptions(): Options {
    return {
      autoResize: true,
      height: "500px",
      width: "100%",
      physics: { enabled: true },
      layout: {
        randomSeed: undefined,
        improvedLayout: true,
        hierarchical: {
          enabled: true,
          levelSeparation: 170,
          direction: "UD", // UD, DU, LR, RL
          sortMethod: "directed", // hubsize, directed
          nodeSpacing: 100
        }
      },
      nodes: {
        scaling: {
          min: 150,
          max: 160,
          label: {
            enabled: false,
            min: 14,
            max: 30,
            maxVisible: 40,
            drawThreshold: 5
          },
          customScalingFunction: (
              min: number,
              max: number,
              total: number,
              value: number
          ) => {
            if (max === min) {
              return 0.5;
            } else {
              let scale = 1 / (max - min);
              return Math.max(0, (value - min) * scale);
            }
          }
        },
        size: 10,
        //color: "#F06292",
        // color: "#fff",

        font: {
          size: 20,
          color: "#ffffff"
        }
      }
    };
  }

}
