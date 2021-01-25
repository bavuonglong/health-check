import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import AppNavbar from './../AppNavbar';

class ServiceEdit extends Component {

  emptyItem = {
    name: '',
    url: ''
  };

  constructor(props) {
    super(props);
    this.state = {
      item: this.emptyItem
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  async componentDidMount() {
    if (this.props.match.params.id !== 'new') {
      const response = await fetch(`/api/v1/services/${this.props.match.params.id}`)
      if (!response.ok) {
          alert("Service is not exists ")
          this.props.history.push('/services')
      }
      const service = await response.json();
      this.setState({item: service.data});
    }
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let item = {...this.state.item};
    item[name] = value;
    this.setState({item});
  }

  async handleSubmit(event) {
    event.preventDefault();
    const {item} = this.state;

    const response = await fetch('/api/v1/services', {
      method: (item.id) ? 'PUT' : 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(item),
    });

    const service = await response.json()
    if (response.ok) {
        this.props.history.push('/services/view/'+ service.data.id);
    } else {
        alert(service.developer_message);
    }
  }

  render() {
    const {item} = this.state;
    const title = <h2>{item.id ? 'Edit Service' : 'Add Service'}</h2>;

    return <div>
      <AppNavbar/>
      <Container>
        {title}
        <Form onSubmit={this.handleSubmit}>
          <FormGroup>
            <Label for="name">Name</Label>
            <Input type="text" name="name" id="name" value={item.name || ''}
                   onChange={this.handleChange} autoComplete="name"/>
          </FormGroup>
          <FormGroup>
            <Label for="url">URL</Label>
            <Input type="text" name="url" id="url" value={item.url || ''}
                   onChange={this.handleChange} autoComplete="url"/>
          </FormGroup>
          <FormGroup>
            <Button color="primary" type="submit">Save</Button>{' '}
            <Button color="secondary" tag={Link} to="/services">Cancel</Button>
          </FormGroup>
        </Form>
      </Container>
    </div>
  }
}

export default withRouter(ServiceEdit);