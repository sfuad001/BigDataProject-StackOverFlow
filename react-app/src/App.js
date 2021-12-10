import React from 'react';
import logo from './logo.svg';
import axios from 'axios';
import './App.css';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Image from 'react-bootstrap/Image';
import Figure from 'react-bootstrap/Figure';
import { PieChart } from './PieChart';
import { BarChart } from './BarChart';
import clusterImg from './cluster.png';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      postCountVsLocationSortedDataOptions: {},
      trendsOptions: {}
    };
  }

  getPostCountVsLocationSortedData() {
    axios.get('http://localhost:3001/location/post-count-location-sorted').then((response) => {
      console.log(response.data.jsonData);
      let size;
      const options = {
        title: {
          text: "Post Count Vs Location Bar Chart"
        },
        axisX: {
          interval: 1,
        },
        axisY: {
          title: "Post Count (100K)"
        }
      };

      if (response.data.jsonData.length > 20) {
        size = 20;
      } else {
        size = response.data.jsonData.length;
      }

      const data = [];
      const dataPoints = [];

      for (let i = 0; i < size; i++) {
        let y = parseFloat(response.data.jsonData[i]["count"]) / 100000;
        dataPoints.push({
          y: y,
          label: response.data.jsonData[i]["Location"]
        })
      }

      const json = {
        type: "column",
        animationEnabled: true,
        dataPoints: dataPoints
      };

      data.push(json);

      options.data = data;
      console.log(options);

      this.setState({
        postCountVsLocationSortedDataOptions: options
      })
    }).catch(err => {
      console.log(err);
    });
  }




  getTrendsData(fileName) {
    axios.get('http://localhost:3001/trends/particular-year-trend', {
      params: {
        fileName: fileName
      }
    }).then((response) => {
      console.log(response.data.jsonData);
      let size;
      const options = {
        title: {
          text: "Language trends of " + response.data.year
        },
        axisY: {
          title: "User Count (10K)"
        }
      };

      if (response.data.jsonData.length > 10) {
        size = 10;
      } else {
        size = response.data.jsonData.length;
      }

      const data = [];
      const dataPoints = [];

      for (let i = 0; i < size; i++) {
        let y = parseFloat(response.data.jsonData[i]["count"]) / 10000;
        dataPoints.push({
          label: response.data.jsonData[i]["tagName"],
          y: y
        })
      }

      const json = {
        type: "pie",
        startAngle: 75,
        toolTipContent: "<b>{label}</b>: {y}%",
        showInLegend: "true",
        legendText: "{label}",
        indexLabelFontSize: 16,
        indexLabel: "{label} - {y}%",
        dataPoints: dataPoints
      };

      data.push(json);

      options.data = data;
      console.log(options);

      this.setState({
        trendsOptions: options
      })
    }).catch(err => {
      console.log(err);
    });
  }

  componentDidMount() {
    this.getPostCountVsLocationSortedData();
    const fileName = "2014.csv";
    this.getTrendsData(fileName);
  }

  render() {
    return (
      <Container className="p-3">
        <Row className="py-3">
          <Col className="my-3 mx-auto">
            <BarChart options={this.state.postCountVsLocationSortedDataOptions} />
          </Col>
        </Row>
        <Row>
          <Col className='text-center'>
            <Image src={clusterImg} width={800} height={500} />
          </Col>
          <div>
            <p>
              <strong>C1:</strong> (python, python3),
              <strong>C2:</strong> (ruby-on-rails, ruby),
              <strong>C3:</strong> (swift, xcode, ios),
              <strong>C4:</strong> (javascript, jquery),
              <strong>C5:</strong> (html, css),
              <strong>C6:</strong> (pandas, django, numpy, matplotlib),
              <strong>C7:</strong> (objective-c),
              <strong>C8:</strong> (android, java, html5, monaca, onsen-ui, node.js, swift3),
              <strong>C9:</strong> (C#, .net, visual-studio, unity3d),
              <strong>C10:</strong> (php, mysql, laravel)
            </p>
          </div>
        </Row>
        <Row className="py-3">
          <Col className="my-3 mx-auto">
            <PieChart options={this.state.trendsOptions} />
          </Col>
        </Row>
      </Container>
    );
  }
}

export default App;
