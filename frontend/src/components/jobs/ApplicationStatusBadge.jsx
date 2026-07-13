import Badge from '../ui/Badge';
import { formatApplicationStatus, applicationStatusTone } from '../../services/applicationService';

export default function ApplicationStatusBadge({ status }) {
  return <Badge tone={applicationStatusTone(status)}>{formatApplicationStatus(status)}</Badge>;
}
