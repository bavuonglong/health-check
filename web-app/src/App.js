import React, { Component } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ServiceList from './service/ServiceList';
import ServiceEdit from './service/ServiceEdit';
import ServiceView from './service/ServiceView';

class App extends Component {
  render() {
    return (
      <Router>
        <Switch>
          <Route path='/' exact={true} component={ServiceList}/>
          <Route path='/services' exact={true} component={ServiceList}/>
          <Route path='/services/:id' exact={true} component={ServiceEdit}/>
          <Route path='/services/view/:id' exact={true} component={ServiceView}/>
        </Switch>
      </Router>
    )
  }
}

export default App;