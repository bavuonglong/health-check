import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Input, Label } from 'reactstrap';
import AppNavbar from './../AppNavbar';

class ServiceView extends Component {

  emptyItem = {
    name: '',
    url: ''
  };

  constructor(props) {
    super(props);
    this.state = {
      item: this.emptyItem
    };
  }

  async componentDidMount() {
      const response = await fetch(`/api/v1/services/${this.props.match.params.id}`)
      if (!response.ok) {
          alert("Service is not exists ")
          this.props.history.push('/services')
      }
      const service = await response.json();
      this.setState({item: service.data});
  }

  render() {
    const {item} = this.state;
    const title = <h2>View Service</h2>;

    return <div>
      <AppNavbar/>
      <Container>
        {title}
            <Label>Name</Label>
            <Input value={item.name}/>

            <Label>URL</Label>
            <Input value={item.url}/>

            <Label>Status</Label>
            <Input value={item.status}/>

            <Label>Date Created</Label>
            <Input value={item.date_created}/>

            <Label>Last Updated</Label>
            <Input value={item.last_updated}/>

            <Button color="secondary" tag={Link} to={"/services/" + item.id}>Edit</Button>
            <Button color="primary" tag={Link} to="/services">Back to services</Button>
      </Container>
    </div>
  }
}

export default ServiceView;