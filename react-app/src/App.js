import React from 'react';
import logo from './logo.svg';
import './App.css';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { PieChart } from './PieChart';
import { BarChart } from './BarChart';

class App extends React.Component {
  render() {
    return (
      <Container className="p-3">
        <Row className="py-3">
          <Col className="my-3 mx-auto">
            <BarChart />
          </Col>
        </Row>
        <Row className="py-3">
          <Col className="my-3 mx-auto">
            <PieChart />
          </Col>
        </Row>
      </Container>
    );
  }
}

export default App;
