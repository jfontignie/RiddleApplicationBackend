import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Riddle from './riddle';
import RiddleDetail from './riddle-detail';
import RiddleUpdate from './riddle-update';
import RiddleDeleteDialog from './riddle-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RiddleUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RiddleUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RiddleDetail} />
      <ErrorBoundaryRoute path={match.url} component={Riddle} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={RiddleDeleteDialog} />
  </>
);

export default Routes;
